package com.github.kr328.clipboard;

import android.content.Context;
import android.content.IClipboard;
import android.os.Binder;
import android.os.Process;

import com.github.kr328.clipboard.shared.Log;
import com.github.kr328.magic.proxy.AIDLProxy;
import com.github.kr328.magic.proxy.ServiceManagerProxy;
import com.github.kr328.zloader.ZygoteLoader;

import java.util.Properties;

public class Injector {
    @SuppressWarnings("unused")
    public static void main(String processName, Properties properties) {
        Log.i("Injected into " + processName);
        Log.i("Uid " + Process.myUid());
        Log.i("Pid " + Process.myPid());

        if (!ZygoteLoader.PACKAGE_SYSTEM_SERVER.equals(processName)) {
            return;
        }

        try {
            new ServiceManagerProxy.Builder()
                    .setAddServiceFilter(Injector::replaceService)
                    .build()
                    .install();

            Log.i("Inject successfully");
        } catch (Throwable e) {
            Log.e("Inject: " + e, e);
        }
    }

    private static Binder replaceService(String name, Binder service) {
        if (Context.CLIPBOARD_SERVICE.equals(name)) {
            try {
                final IClipboard original = IClipboard.Stub.asInterface(service);

                return AIDLProxy.newServer(IClipboard.class, original, new ClipboardProxy(original));
            } catch (Throwable e) {
                Log.e("Proxy clipboard: " + e, e);
            }
        }

        return service;
    }
}
