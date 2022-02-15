package com.github.kr328.clipboard;

import android.content.Context;
import android.content.IClipboard;
import android.os.Binder;
import android.os.Process;

import com.github.kr328.clipboard.shared.Log;
import com.github.kr328.magic.services.ServiceManagerProxy;
import com.github.kr328.zloader.ZygoteLoader;

public class Main {
    @SuppressWarnings("unused")
    public static void main() {
        Log.i("Injected into " + ZygoteLoader.getPackageName());
        Log.i("Uid " + Process.myUid());
        Log.i("Pid " + Process.myPid());

        if (!ZygoteLoader.PACKAGE_SYSTEM_SERVER.equals(ZygoteLoader.getPackageName())) {
            return;
        }

        try {
            new ServiceManagerProxy.Builder()
                    .setAddServiceFilter(Main::replaceClipboard)
                    .build()
                    .install();

            Log.i("Inject successfully");
        } catch (Throwable e) {
            Log.e("Inject: " + e, e);
        }
    }

    private static Binder replaceClipboard(String name, Binder service) {
        if (Context.CLIPBOARD_SERVICE.equals(name)) {
            Log.i("Replacing clipboard");

            try {
                final IClipboard original = IClipboard.Stub.asInterface(service);

                return ClipboardProxy.FACTORY.create(original, new ClipboardProxy(original));
            } catch (Throwable e) {
                Log.e("Replacing clipboard: " + e, e);
            }
        }

        return service;
    }
}
