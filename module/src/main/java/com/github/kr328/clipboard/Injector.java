package com.github.kr328.clipboard;

import static com.github.kr328.clipboard.shared.Constants.TAG;

import android.content.Context;
import android.content.IClipboard;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import java.util.Properties;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class Injector extends ServiceProxy {
    public static void main(String processName, Properties properties) {
        Log.i(TAG, String.format("Process = %s Uid = %d Pid = %d", processName, Process.myUid(), Process.myPid()));

        Injector injector = new Injector();

        try {
            injector.install();

            Log.i(TAG, "Inject successfully");
        } catch (Exception e) {
            Log.e(TAG, "Inject failure", e);
        }
    }

    @Override
    protected IBinder onAddService(String name, IBinder service) {
        if (Context.CLIPBOARD_SERVICE.equals(name)) {
            try {
                return ProxyFactory.instance(service, new ClipboardProxy(IClipboard.Stub.asInterface(service)));
            } catch (Exception e) {
                Log.e(TAG, "Proxy ClipboardManager failure", e);
            }
        }

        return super.onAddService(name, service);
    }
}
