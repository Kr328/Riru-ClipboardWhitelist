package com.github.kr328.clipboard;

import android.app.ActivityThread;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.IClipboard;
import android.content.IOnPrimaryClipChangedListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerHidden;
import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.SettingsSecure;
import android.text.TextUtils;

import com.github.kr328.clipboard.shared.Constants;
import com.github.kr328.clipboard.shared.Log;
import com.github.kr328.magic.aidl.ServerProxy;
import com.github.kr328.magic.aidl.ServerProxyFactory;
import com.github.kr328.magic.aidl.TransactProxy;
import com.github.kr328.magic.util.BinderUtils;

import java.util.Arrays;

import dev.rikka.tools.refine.Refine;

public class ClipboardProxy extends IClipboard.Stub {
    public final static ServerProxyFactory<IClipboard, ClipboardProxy> FACTORY =
            ServerProxy.mustCreateFactory(IClipboard.class, ClipboardProxy.class, false);

    private final IClipboard original;

    private final WhitelistService whitelistService = new WhitelistService();

    public ClipboardProxy(IClipboard original) {
        this.original = original;
    }

    @Override
    @TransactProxy
    public ClipData getPrimaryClip(String callingPackage, int userId) throws RemoteException {
        final String packageName = markAsDefaultIME(callingPackage, userId);

        return original.getPrimaryClip(packageName, userId);
    }

    @Override
    @TransactProxy
    public ClipDescription getPrimaryClipDescription(String callingPackage, int userId) throws RemoteException {
        final String packageName = markAsDefaultIME(callingPackage, userId);

        return original.getPrimaryClipDescription(packageName, userId);
    }

    @Override
    @TransactProxy
    public boolean hasPrimaryClip(String callingPackage, int userId) throws RemoteException {
        final String packageName = markAsDefaultIME(callingPackage, userId);

        return original.hasPrimaryClip(packageName, userId);
    }

    @Override
    @TransactProxy
    public boolean hasClipboardText(String callingPackage, int userId) throws RemoteException {
        final String packageName = markAsDefaultIME(callingPackage, userId);

        return original.hasClipboardText(packageName, userId);
    }

    @Override
    @TransactProxy
    public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        final String packageName = markAsDefaultIME(callingPackage, userId);

        original.addPrimaryClipChangedListener(listener, packageName, userId);
    }

    @Override
    @TransactProxy
    public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        final String packageName = markAsDefaultIME(callingPackage, userId);

        original.removePrimaryClipChangedListener(listener, packageName, userId);
    }

    @Override
    @TransactProxy(Constants.TRANSACT_CODE_GET_SERVICE)
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (Constants.TRANSACT_CODE_GET_SERVICE == code) {
            final Context context = ActivityThread.currentActivityThread().getSystemContext();

            if (context == null)
                return false;

            try {
                final PackageManager pm = context.getPackageManager();
                if (pm == null) {
                    return false;
                }

                final String[] packages = pm.getPackagesForUid(Binder.getCallingUid());
                if (packages == null) {
                    return false;
                }

                if (!Arrays.asList(packages).contains(Constants.APP_PACKAGE_NAME)) {
                    return false;
                }
            } catch (Exception e) {
                Log.e("Verify manager failed: " + e, e);

                return false;
            }

            reply.writeStrongBinder(whitelistService);

            return true;
        }

        return super.onTransact(code, data, reply, flags);
    }

    private String markAsDefaultIME(String callingPkg, int userId) {
        try {
            final Context context = ActivityThread.currentActivityThread().getSystemContext();
            if (context == null) {
                throw new IllegalStateException("Context unavailable");
            }

            final PackageManagerHidden pm = Refine.unsafeCast(context.getPackageManager());
            if (pm == null) {
                throw new IllegalStateException("PackageManager unavailable");
            }

            if (pm.getPackageUidAsUser(callingPkg, userId) != Binder.getCallingUid()) {
                throw new IllegalStateException("Uid of callingPkg != callingUid");
            }

            if (!DataStore.instance.isExempted(callingPkg, userId)) {
                return callingPkg;
            }

            final String componentName = BinderUtils.withEvaluated(() ->
                    SettingsSecure.getStringForUser(
                            context.getContentResolver(),
                            Settings.Secure.DEFAULT_INPUT_METHOD,
                            userId
                    )
            );
            if (TextUtils.isEmpty(componentName)) {
                throw new IllegalStateException("Unable to get componentName of default IME");
            }

            final String packageName = ComponentName.unflattenFromString(componentName).getPackageName();
            final int targetUid = BinderUtils.withEvaluated(() -> pm.getPackageUidAsUser(packageName, userId));
            final long newToken = (Binder.clearCallingIdentity() & 0xFFFFFFFFL) | (((long) targetUid) << 32);

            Binder.restoreCallingIdentity(newToken);

            return packageName;
        } catch (Exception e) {
            Log.w("Run as default IME: " + e, e);

            return callingPkg;
        }
    }
}
