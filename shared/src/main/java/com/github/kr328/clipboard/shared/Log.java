package com.github.kr328.clipboard.shared;

public final class Log {
    private static final String TAG = "ClipboardWhitelist";

    public static void i(String msg, Throwable throwable) {
        android.util.Log.i(TAG, msg, throwable);
    }

    public static void i(String msg) {
        android.util.Log.i(TAG, msg);
    }

    public static void e(String msg, Throwable throwable) {
        android.util.Log.e(TAG, msg, throwable);
    }

    public static void e(String msg) {
        android.util.Log.e(TAG, msg);
    }

    public static void w(String msg, Throwable throwable) {
        android.util.Log.w(TAG, msg, throwable);
    }

    public static void w(String msg) {
        android.util.Log.w(TAG, msg);
    }
}
