package com.github.kr328.clipboard;

import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.io.FileDescriptor;

public class ClipboardProxy extends Binder {
    private final IBinder original;
    private final DataStore dataStore;

    ClipboardProxy(IBinder original, IPackageManager packageManager) {
        this.original = original;
        this.dataStore = new DataStore(packageManager);

        dataStore.postLoad();
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {


        return original.transact(code, data, reply, flags);
    }

    @Override
    public String getInterfaceDescriptor() {
        try {
            return original.getInterfaceDescriptor();
        } catch (RemoteException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean pingBinder() {
        return original.pingBinder();
    }

    @Override
    public boolean isBinderAlive() {
        return original.isBinderAlive();
    }

    @Override
    public IInterface queryLocalInterface(String descriptor) {
        return null;
    }

    @Override
    public void attachInterface(IInterface owner, String descriptor) {
        super.attachInterface(owner, descriptor);
    }

    @Override
    public void dump(FileDescriptor fd, String[] args) {
        try {
            original.dump(fd, args);
        } catch (RemoteException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void dumpAsync(FileDescriptor fd, String[] args) {
        try {
            original.dumpAsync(fd, args);
        } catch (RemoteException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void linkToDeath(DeathRecipient recipient, int flags) {
        try {
            original.linkToDeath(recipient, flags);
        } catch (RemoteException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
        return original.unlinkToDeath(recipient, flags);
    }
}
