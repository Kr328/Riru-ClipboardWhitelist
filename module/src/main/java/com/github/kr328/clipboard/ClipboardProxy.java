package com.github.kr328.clipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.IClipboard;
import android.content.IOnPrimaryClipChangedListener;
import android.os.RemoteException;

import com.github.kr328.clipboard.ProxyFactory.TransactHook;

public class ClipboardProxy extends IClipboard.Stub {
    private final IClipboard original;

    ClipboardProxy(IClipboard original) {
        this.original = original;
    }

    @Override
    @TransactHook
    public ClipData getPrimaryClip(String pkg, int userId) throws RemoteException {
        return original.getPrimaryClip(pkg, userId);
    }

    @Override
    @TransactHook
    public ClipDescription getPrimaryClipDescription(String callingPackage, int userId) throws RemoteException {
        return original.getPrimaryClipDescription(callingPackage, userId);
    }

    @Override
    @TransactHook
    public boolean hasPrimaryClip(String callingPackage, int userId) throws RemoteException {
        return original.hasPrimaryClip(callingPackage, userId);
    }

    @Override
    @TransactHook
    public boolean hasClipboardText(String callingPackage, int userId) throws RemoteException {
        return original.hasClipboardText(callingPackage, userId);
    }

    @Override
    @TransactHook
    public void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        original.addPrimaryClipChangedListener(listener, callingPackage, userId);
    }

    @Override
    @TransactHook
    public void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener, String callingPackage, int userId) throws RemoteException {
        original.removePrimaryClipChangedListener(listener, callingPackage, userId);
    }
}
