package com.github.kr328.clipboard;

import android.app.ActivityThread;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.IClipboard;
import android.content.IOnPrimaryClipChangedListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.github.kr328.clipboard.ProxyFactory.TransactHook;
import com.github.kr328.clipboard.shared.Constants;

import $android.content.pm.PackageManager;

public class ClipboardProxy extends IClipboard.Stub {
    private final IClipboard original;
    private final WhitelistService whitelistService = new WhitelistService();

    ClipboardProxy(IClipboard original) {
        this.original = original;
    }

    @Override
    @TransactHook
    public ClipData getPrimaryClip(String callingPackage, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(callingPackage))
            return original.getPrimaryClip(callingPackage, userId);

        String packageName = asDefaultIME(callingPackage, userId);

        return original.getPrimaryClip(packageName, userId);
    }

    @Override
    @TransactHook
    public ClipDescription getPrimaryClipDescription(String callingPackage, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(callingPackage))
            return original.getPrimaryClipDescription(callingPackage, userId);

        String packageName = asDefaultIME(callingPackage, userId);

        return original.getPrimaryClipDescription(packageName, userId);
    }

    @Override
    @TransactHook
    public boolean hasPrimaryClip(String callingPackage, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(callingPackage))
            return original.hasPrimaryClip(callingPackage, userId);

        String packageName = asDefaultIME(callingPackage, userId);

        return original.hasPrimaryClip(packageName, userId);
    }

    @Override
    @TransactHook
    public boolean hasClipboardText(String callingPackage, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(callingPackage))
            return original.hasClipboardText(callingPackage, userId);

        String packageName = asDefaultIME(callingPackage, userId);

        return original.hasClipboardText(packageName, userId);
    }

    @Override
    @TransactHook
    public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(callingPackage)) {
            original.addPrimaryClipChangedListener(listener, callingPackage, userId);

            return;
        }

        String packageName = asDefaultIME(callingPackage, userId);

        original.addPrimaryClipChangedListener(listener, packageName, userId);
    }

    @Override
    @TransactHook
    public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(callingPackage)) {
            original.removePrimaryClipChangedListener(listener, callingPackage, userId);

            return;
        }

        String packageName = asDefaultIME(callingPackage, userId);

        original.removePrimaryClipChangedListener(listener, packageName, userId);
    }

    @Override
    @TransactHook(Constants.TRANSACT_CODE_GET_SERVICE)
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (Constants.TRANSACT_CODE_GET_SERVICE == code) {
            Context context = ActivityThread.currentActivityThread().getSystemContext();

            if (context == null)
                return false;

            PackageManager pm = Unsafe.unsafeCast(context.getPackageManager());

            try {
                if (pm.getPackageUidAsUser(Constants.APP_PACKAGE_NAME, 0)
                        != Binder.getCallingUid())
                    return super.onTransact(code, data, reply, flags);
            } catch (NameNotFoundException e) {
                return false;
            }

            reply.writeStrongBinder(whitelistService);

            return true;
        }

        return super.onTransact(code, data, reply, flags);
    }

    private String asDefaultIME(String callingPkg, int userId) {
        try {
            Context context = ActivityThread.currentActivityThread().getSystemContext();

            if (context == null)
                return callingPkg;

            PackageManager pm = Unsafe.unsafeCast(context.getPackageManager());

            if (pm.getPackageUidAsUser(callingPkg, userId) != Binder.getCallingUid())
                return callingPkg;

            String componentName = $android.provider.Settings$Secure.getStringForUser(
                    context.getContentResolver(),
                    Settings.Secure.DEFAULT_INPUT_METHOD,
                    userId
            );

            if (TextUtils.isEmpty(componentName))
                return callingPkg;

            String packageName = ComponentName.unflattenFromString(componentName).getPackageName();

            int targetUid = pm.getPackageUidAsUser(packageName, userId);

            long token = Binder.clearCallingIdentity();
            long newToken = token & 0xFFFFFFFFL | ((long) targetUid << 32);

            Binder.restoreCallingIdentity(newToken);

            return packageName;
        } catch (Exception e) {
            Log.w(Constants.TAG, "Run as default IME: " + e, e);

            return callingPkg;
        }
    }
}
