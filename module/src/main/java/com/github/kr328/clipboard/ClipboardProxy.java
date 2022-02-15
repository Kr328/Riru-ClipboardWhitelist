package com.github.kr328.clipboard;

import android.app.ActivityThread;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.IClipboard;
import android.content.IOnPrimaryClipChangedListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerHidden;
import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

import com.github.kr328.clipboard.shared.Constants;
import com.github.kr328.clipboard.shared.Log;
import com.github.kr328.magic.aidl.ServerProxy;
import com.github.kr328.magic.aidl.ServerProxyFactory;
import com.github.kr328.magic.aidl.TransactProxy;

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
        final String packageName = markAsSystem(callingPackage, userId);

        return original.getPrimaryClip(packageName, userId);
    }

    @Override
    @TransactProxy
    public ClipDescription getPrimaryClipDescription(String callingPackage, int userId) throws RemoteException {
        final String packageName = markAsSystem(callingPackage, userId);

        return original.getPrimaryClipDescription(packageName, userId);
    }

    @Override
    @TransactProxy
    public boolean hasPrimaryClip(String callingPackage, int userId) throws RemoteException {
        final String packageName = markAsSystem(callingPackage, userId);

        return original.hasPrimaryClip(packageName, userId);
    }

    @Override
    @TransactProxy
    public boolean hasClipboardText(String callingPackage, int userId) throws RemoteException {
        final String packageName = markAsSystem(callingPackage, userId);

        return original.hasClipboardText(packageName, userId);
    }

    @Override
    @TransactProxy
    public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        final String packageName = markAsSystem(callingPackage, userId);

        original.addPrimaryClipChangedListener(listener, packageName, userId);
    }

    @Override
    @TransactProxy
    public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        final String packageName = markAsSystem(callingPackage, userId);

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

    private String markAsSystem(String callingPkg, int userId) {
        try {
            final Context context = ActivityThread.currentActivityThread().getSystemContext();
            if (context == null) {
                return callingPkg;
            }

            final PackageManagerHidden pm = Refine.unsafeCast(context.getPackageManager());
            if (pm == null) {
                return callingPkg;
            }

            if (pm.getPackageUidAsUser(callingPkg, userId) != Binder.getCallingUid()) {
                return callingPkg;
            }

            if (!DataStore.instance.isExempted(callingPkg, userId)) {
                return callingPkg;
            }

            Binder.clearCallingIdentity();

            return context.getPackageName();
        } catch (Exception e) {
            Log.w("Run as system: " + e, e);

            return callingPkg;
        }
    }
}
