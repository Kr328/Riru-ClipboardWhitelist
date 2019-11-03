package com.github.kr328.clipboard;

import android.content.Context;
import android.content.IClipboard;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemProperties;
import android.util.Log;
import com.github.kr328.clipboard.proxy.ProxyBinderFactory;
import com.github.kr328.clipboard.proxy.ServiceManagerProxy;

@SuppressWarnings("unused")
public class Injector {
    private static Binder clipboard;
    private static ClipboardProxy proxy;

    public static void inject(String placeholder) {
        Log.i(Constants.TAG, "In system_server pid = " + android.os.Process.myPid());

        try {
            ServiceManagerProxy.install(new ServiceManagerProxy.Callback() {
                @Override
                public IBinder addService(String name, IBinder original) {
                    if (Context.CLIPBOARD_SERVICE.equals(name)) {
                        if (proxy == null)
                            proxy = new ClipboardProxy();

                        proxy.setClipboard(IClipboard.Stub.asInterface(original));

                        try {
                            clipboard = (Binder) original;
                            Binder result = ProxyBinderFactory.createProxyBinder(clipboard, proxy);
                            SystemProperties.set(Constants.LOADED_PROPERTIES, "loaded");
                            return result;
                        } catch (Exception e) {
                            Log.e(Constants.TAG, "Create proxy failure", e);
                            return original;
                        }
                    } else if ("package".equals(name)) {
                        if (proxy == null)
                            proxy = new ClipboardProxy();
                        proxy.setPackageManager(IPackageManager.Stub.asInterface(original));
                    }

                    return original;
                }

                @Override
                public IBinder getService(String name, IBinder original) {
                    if (original != null && Context.CLIPBOARD_SERVICE.equals(name))
                        return clipboard;
                    return original;
                }

                @Override
                public IBinder checkService(String name, IBinder original) {
                    if (original != null && Context.CLIPBOARD_SERVICE.equals(name))
                        return clipboard;
                    return original;
                }
            });

            Log.i(Constants.TAG, "Inject successfully");
        } catch (Exception e) {
            Log.e(Constants.TAG, "Inject failure", e);
        }
    }
}
