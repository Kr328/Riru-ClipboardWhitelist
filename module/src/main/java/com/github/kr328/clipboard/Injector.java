package com.github.kr328.clipboard;

import android.content.Context;
import android.content.IClipboard;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;

import com.github.kr328.clipboard.shared.Log;
import com.github.kr328.clipboard.util.ProxyFactory;
import com.github.kr328.clipboard.util.ServiceProxy;
import com.github.kr328.zloader.ZygoteLoader;

import java.util.Properties;

public class Injector extends ServiceProxy {
    @SuppressWarnings("unused")
    public static void main(String processName, Properties properties) {
        Log.i("Injected into " + processName);
        Log.i("Uid " + Process.myUid());
        Log.i("Pid " + Process.myPid());

        if (!ZygoteLoader.PACKAGE_SYSTEM_SERVER.equals(processName)) {
            return;
        }

        Injector injector = new Injector();

        try {
            injector.install();

            Log.i("Inject successfully");
        } catch (Exception e) {
            Log.e("Inject: " + e, e);
        }
    }

    @Override
    protected IBinder onAddService(String name, IBinder service) {
        if (Context.CLIPBOARD_SERVICE.equals(name)) {
            try {
                return ProxyFactory.instance((Binder) service, new ClipboardProxy(IClipboard.Stub.asInterface(service)));
            } catch (Exception e) {
                Log.e("Proxy clipboard: " + e, e);
            }
        }

        return super.onAddService(name, service);
    }
}
