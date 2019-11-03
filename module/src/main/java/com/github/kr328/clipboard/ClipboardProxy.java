package com.github.kr328.clipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.IClipboard;
import android.content.IOnPrimaryClipChangedListener;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import com.github.kr328.clipboard.proxy.ProxyBinderFactory.ReplaceTransact;

public class ClipboardProxy extends IClipboard.Stub {
    private final static String SHELL_PACKAGE = "com.android.shell";
    private IClipboard original;
    private IPackageManager pm;
    private DataStore dataStore = new DataStore();

    ClipboardProxy() {
        dataStore.startWatching();
        dataStore.postLoad();
    }

    public void setClipboard(IClipboard clipboard) {
        this.original = clipboard;
    }

    void setPackageManager(IPackageManager pm) {
        this.pm = pm;
    }

    private <T> T runAsShell(String methodName, RunAsShellFunctionWithResult<T> function) throws RemoteException {
        Log.v(Constants.TAG, "" + Binder.getCallingUid() + ": " + methodName);

        long token = Binder.clearCallingIdentity();
        long shellToken = token & 0xFFFFFFFFL | ((long) (Process.SHELL_UID) << 32);

        Binder.restoreCallingIdentity(shellToken);

        T result = function.call();

        Binder.restoreCallingIdentity(token);

        return result;
    }

    private void runAsShell(String methodName, RunAsShellFunctionVoid function) throws RemoteException {
        runAsShell(methodName, () -> {
            function.call();
            return true;
        });
    }

    @Override
    @ReplaceTransact
    public ClipData getPrimaryClip(String pkg, int userId) throws RemoteException {
        if (dataStore.getPackages().contains(pkg) && pm.getPackageUid(pkg, 0, userId) == Binder.getCallingUid())
            return runAsShell("getPrimaryClip", () -> original.getPrimaryClip(SHELL_PACKAGE, userId));

        return original.getPrimaryClip(pkg, userId);
    }

    @Override
    @ReplaceTransact
    public ClipDescription getPrimaryClipDescription(String callingPackage, int userId) throws RemoteException {
        if (dataStore.getPackages().contains(callingPackage) && pm.getPackageUid(callingPackage, 0, userId) == Binder.getCallingUid())
            return runAsShell("getPrimaryClipDescription", () -> original.getPrimaryClipDescription(SHELL_PACKAGE, userId));

        return original.getPrimaryClipDescription(callingPackage, userId);
    }

    @Override
    @ReplaceTransact
    public boolean hasPrimaryClip(String callingPackage, int userId) throws RemoteException {
        if (dataStore.getPackages().contains(callingPackage) && pm.getPackageUid(callingPackage, 0, userId) == Binder.getCallingUid())
            return runAsShell("hasPrimaryClip", () -> original.hasPrimaryClip(SHELL_PACKAGE, userId));

        return original.hasPrimaryClip(callingPackage, userId);
    }

    @Override
    @ReplaceTransact
    public boolean hasClipboardText(String callingPackage, int userId) throws RemoteException {
        if (dataStore.getPackages().contains(callingPackage) && pm.getPackageUid(callingPackage, 0, userId) == Binder.getCallingUid())
            return runAsShell("hasClipboardText", () -> original.hasClipboardText(SHELL_PACKAGE, userId));

        return original.hasClipboardText(callingPackage, userId);
    }

    @Override
    @ReplaceTransact
    public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        if (dataStore.getPackages().contains(callingPackage) && pm.getPackageUid(callingPackage, 0, userId) == Binder.getCallingUid())
            runAsShell("addPrimaryClipChangedListener", () -> original.addPrimaryClipChangedListener(listener, SHELL_PACKAGE, userId));
        else
            original.addPrimaryClipChangedListener(listener, callingPackage, userId);
    }

    @Override
    @ReplaceTransact
    public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        if (dataStore.getPackages().contains(callingPackage) && pm.getPackageUid(callingPackage, 0, userId) == Binder.getCallingUid())
            runAsShell("removePrimaryClipChangedListener", () -> original.removePrimaryClipChangedListener(listener, SHELL_PACKAGE, userId));
        else
            original.removePrimaryClipChangedListener(listener, callingPackage, userId);
    }

    private interface RunAsShellFunctionWithResult<T> {
        T call() throws RemoteException;
    }

    private interface RunAsShellFunctionVoid {
        void call() throws RemoteException;
    }
}
