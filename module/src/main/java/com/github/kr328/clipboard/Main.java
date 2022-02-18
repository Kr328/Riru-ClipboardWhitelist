package com.github.kr328.clipboard;

import android.app.ActivityManagerHidden;
import android.app.ActivityThread;
import android.content.Context;
import android.content.IClipboard;
import android.os.Binder;
import android.os.Process;

import com.github.kr328.clipboard.shared.Log;
import com.github.kr328.magic.services.ServiceManagerProxy;
import com.github.kr328.zloader.ZygoteLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

            new Thread(() -> {
                final Path dataDirectory = Paths.get(ZygoteLoader.getDataDirectory());

                while (!Files.isDirectory(dataDirectory)) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                DataStore.instance.reload();

                try {
                    final ActivityThread thread = ActivityThread.currentActivityThread();
                    if (thread == null) {
                        return;
                    }

                    final Context context = thread.getSystemContext();
                    if (context == null) {
                        return;
                    }

                    final ActivityManagerHidden activity = context.getSystemService(ActivityManagerHidden.class);
                    if (activity == null) {
                        return;
                    }

                    DataStore.instance.getAllExempted().forEach((userId, packages) ->
                            packages.forEach(packageName ->
                                    activity.forceStopPackageAsUser(packageName, userId)
                            )
                    );
                } catch (Throwable throwable) {
                    Log.w("Forcing stop packages failed: " + throwable, throwable);
                }
            }).start();

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
