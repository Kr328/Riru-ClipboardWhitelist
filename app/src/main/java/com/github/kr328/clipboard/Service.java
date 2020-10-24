package com.github.kr328.clipboard;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.github.kr328.clipboard.shared.Constants;
import com.github.kr328.clipboard.shared.IClipboardWhitelist;

public class Service {
    private static IClipboardWhitelist service;

    public synchronized static IClipboardWhitelist getService() throws RemoteException {
        if (service == null) {
            service = getServiceImpl();
        }

        return service;
    }

    private static IClipboardWhitelist getServiceImpl() throws RemoteException {
        final IBinder bridge = ServiceManager.getService(Context.CLIPBOARD_SERVICE);

        if (bridge == null)
            throw new ServiceNotFoundException();

        final Parcel data = Parcel.obtain();
        final Parcel reply = Parcel.obtain();

        try {
            bridge.transact(Constants.TRANSACT_CODE_GET_SERVICE, data, reply, 0);

            if (reply.dataSize() == 0)
                throw new ServiceNotFoundException();

            final IBinder service = reply.readStrongBinder();

            final IClipboardWhitelist whitelist = IClipboardWhitelist.Stub.asInterface(service);

            if (whitelist == null) {
                throw new ServiceNotFoundException();
            } else if (whitelist.version() != Constants.SERVICE_VERSION) {
                throw new VersionNotMatchedException();
            }

            return whitelist;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    public static class ServiceNotFoundException extends RemoteException {
    }

    public static class VersionNotMatchedException extends RemoteException {
    }
}

