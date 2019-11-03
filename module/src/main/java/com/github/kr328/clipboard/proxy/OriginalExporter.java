package com.github.kr328.clipboard.proxy;

import android.os.Binder;

import java.lang.reflect.Method;

public class OriginalExporter {
    public static Binder exportBinder(Binder current) {
        try {
            while (current != null) {
                Method method = current.getClass().getMethod("getOriginal");

                current = (Binder) method.invoke(current);
            }
        } catch (Exception ignored) {
        }

        return current;
    }
}
