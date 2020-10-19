package com.github.kr328.clipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.IClipboard;
import android.content.IOnPrimaryClipChangedListener;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.github.kr328.clipboard.ProxyFactory.TransactHook;

public class ClipboardProxy extends IClipboard.Stub {
    private final static String SHELL_PACKAGE = "com.android.shell";

    private final DataStore dataStore = new DataStore();
    private final IClipboard original;
    private IPackageManager packageManager;

    ClipboardProxy(IClipboard original) {
        this.original = original;
    }

    @Override
    @TransactHook
    public ClipData getPrimaryClip(String pkg, int userId) throws RemoteException {
        if (dataStore.shouldIgnore(pkg))
            return original.getPrimaryClip(pkg, userId);

        enforcePackageUid(pkg, Binder.getCallingUid(), userId);

        asShell();

        return original.getPrimaryClip(SHELL_PACKAGE, userId);
    }

    @Override
    @TransactHook
    public ClipDescription getPrimaryClipDescription(String callingPackage, int userId) throws RemoteException {
        if (dataStore.shouldIgnore(callingPackage))
            return original.getPrimaryClipDescription(callingPackage, userId);

        enforcePackageUid(callingPackage, Binder.getCallingUid(), userId);

        asShell();

        return original.getPrimaryClipDescription(SHELL_PACKAGE, userId);
    }

    @Override
    @TransactHook
    public boolean hasPrimaryClip(String callingPackage, int userId) throws RemoteException {
        if (dataStore.shouldIgnore(callingPackage))
            return original.hasPrimaryClip(callingPackage, userId);

        enforcePackageUid(callingPackage, Binder.getCallingUid(), userId);

        asShell();

        return original.hasPrimaryClip(SHELL_PACKAGE, userId);
    }

    @Override
    @TransactHook
    public boolean hasClipboardText(String callingPackage, int userId) throws RemoteException {
        if (dataStore.shouldIgnore(callingPackage))
            return original.hasClipboardText(callingPackage, userId);

        enforcePackageUid(callingPackage, Binder.getCallingUid(), userId);

        asShell();

        return original.hasClipboardText(SHELL_PACKAGE, userId);
    }

    @Override
    @TransactHook
    public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        if (dataStore.shouldIgnore(callingPackage)) {
            original.addPrimaryClipChangedListener(listener, callingPackage, userId);

            return;
        }

        enforcePackageUid(callingPackage, Binder.getCallingUid(), userId);

        asShell();

        original.addPrimaryClipChangedListener(listener, SHELL_PACKAGE, userId);
    }

    @Override
    @TransactHook
    public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        if (dataStore.shouldIgnore(callingPackage)) {
            original.removePrimaryClipChangedListener(listener, callingPackage, userId);

            return;
        }

        enforcePackageUid(callingPackage, Binder.getCallingUid(), userId);

        asShell();

        original.removePrimaryClipChangedListener(listener, SHELL_PACKAGE, userId);
    }

    private void asShell() {
        long token = Binder.clearCallingIdentity();
        long shellToken = token & 0xFFFFFFFFL | ((long) (android.os.Process.SHELL_UID) << 32);

        Binder.restoreCallingIdentity(shellToken);
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
