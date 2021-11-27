package com.github.kr328.clipboard.util;

import android.os.Binder;

public final class BinderUtil {
    public static <R, T extends Throwable> R withEvaluated(EvaluatedAction<R, T> action) throws T {
        long token = Binder.clearCallingIdentity();

        try {
            return action.run();
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public static <T extends Throwable> void withEvaluated(EvaluatedVoidAction<T> action) throws T {
        withEvaluated(() -> {
            action.run();
            return null;
        });
    }

    public interface EvaluatedAction<R, T extends Throwable> {
        R run() throws T;
    }

    public interface EvaluatedVoidAction<T extends Throwable> {
        void run() throws T;
    }
}
