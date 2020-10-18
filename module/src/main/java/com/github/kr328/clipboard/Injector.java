package com.github.kr328.clipboard;

import android.os.Process;
import android.util.Log;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class Injector extends ServiceProxy {
    public static final String TAG = "ClipboardWhitelist";

    public static void inject(String argument) {
        Log.i(TAG, String.format("Uid = %d Pid = %d", Process.myUid(), Process.myPid()));

        Injector injector = new Injector();

        try {
            injector.install();

            Log.i(TAG, "Inject successfully");
        } catch (Exception e) {
            Log.e(TAG, "Inject failure", e);
        }
    }
}
