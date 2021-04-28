package com.github.kr328.clipboard;

import android.app.ActivityThread;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.IClipboard;
import android.content.IOnPrimaryClipChangedListener;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.github.kr328.clipboard.ProxyFactory.TransactHook;
import com.github.kr328.clipboard.shared.Constants;

public class ClipboardProxy extends IClipboard.Stub {
    private final IClipboard original;
    private final WhitelistService whitelistService = new WhitelistService();

    private IPackageManager packageManager;

    ClipboardProxy(IClipboard original) {
        this.original = original;
    }

    @Override
    @TransactHook
    public ClipData getPrimaryClip(String pkg, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(pkg))
            return original.getPrimaryClip(pkg, userId);

        enforcePackageUid(pkg, Binder.getCallingUid(), userId);

        String packageName = asDefaultIME(userId);
        if (packageName == null)
            packageName = pkg;

        return original.getPrimaryClip(packageName, userId);
    }

    @Override
    @TransactHook
    public ClipDescription getPrimaryClipDescription(String callingPackage, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(callingPackage))
            return original.getPrimaryClipDescription(callingPackage, userId);

        enforcePackageUid(callingPackage, Binder.getCallingUid(), userId);

        String packageName = asDefaultIME(userId);
        if (packageName == null)
            packageName = callingPackage;

        return original.getPrimaryClipDescription(packageName, userId);
    }

    @Override
    @TransactHook
    public boolean hasPrimaryClip(String callingPackage, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(callingPackage))
            return original.hasPrimaryClip(callingPackage, userId);

        enforcePackageUid(callingPackage, Binder.getCallingUid(), userId);

        String packageName = asDefaultIME(userId);
        if (packageName == null)
            packageName = callingPackage;

        return original.hasPrimaryClip(packageName, userId);
    }

    @Override
    @TransactHook
    public boolean hasClipboardText(String callingPackage, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(callingPackage))
            return original.hasClipboardText(callingPackage, userId);

        enforcePackageUid(callingPackage, Binder.getCallingUid(), userId);

        String packageName = asDefaultIME(userId);
        if (packageName == null)
            packageName = callingPackage;

        return original.hasClipboardText(packageName, userId);
    }

    @Override
    @TransactHook
    public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(callingPackage)) {
            original.addPrimaryClipChangedListener(listener, callingPackage, userId);

            return;
        }

        enforcePackageUid(callingPackage, Binder.getCallingUid(), userId);

        String packageName = asDefaultIME(userId);
        if (packageName == null)
            packageName = callingPackage;

        original.addPrimaryClipChangedListener(listener, packageName, userId);
    }

    @Override
    @TransactHook
    public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        if (DataStore.instance.shouldIgnore(callingPackage)) {
            original.removePrimaryClipChangedListener(listener, callingPackage, userId);

            return;
        }

        enforcePackageUid(callingPackage, Binder.getCallingUid(), userId);

        String packageName = asDefaultIME(userId);
        if (packageName == null)
            packageName = callingPackage;

        original.removePrimaryClipChangedListener(listener, packageName, userId);
    }

    @Override
    @TransactHook(Constants.TRANSACT_CODE_GET_SERVICE)
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (Constants.TRANSACT_CODE_GET_SERVICE == code) {
            if (obtainPackageManager().getPackageUid(Constants.APP_PACKAGE_NAME, 0, 0)
                    != Binder.getCallingUid())
                return super.onTransact(code, data, reply, flags);

            reply.writeStrongBinder(whitelistService);
            return true;
        }

        return super.onTransact(code, data, reply, flags);
    }

    private String asDefaultIME(int userId) throws RemoteException {
        IPackageManager pm = obtainPackageManager();
        Context context = ActivityThread.currentActivityThread().getSystemContext();

        String componentName = $android.provider.Settings$Secure.getStringForUser(
                context.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD,
                userId
        );

        if (TextUtils.isEmpty(componentName))
            return null;

        String packageName = ComponentName.unflattenFromString(componentName).getPackageName();

        int uid = pm.getPackageUid(packageName, 0, userId);

        long token = Binder.clearCallingIdentity();
        long newToken = token & 0xFFFFFFFFL | ((long) uid << 32);

        Binder.restoreCallingIdentity(newToken);

        return packageName;
    }

    private void enforcePackageUid(String packageName, int uid, int userId) throws RemoteException {
        int trustUid = obtainPackageManager().getPackageUid(packageName, 0, userId);

        if (trustUid != uid)
            throw new SecurityException(packageName + "/" + uid + " not matched");
    }

    private synchronized IPackageManager obtainPackageManager() throws RemoteException {
        if (packageManager == null) {
            getCommonServicesLocked();

            if (packageManager == null)
                throw new RemoteException("unable to get package manager");
        }

        return packageManager;
    }

    private void getCommonServicesLocked() {
        packageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
    }
}
