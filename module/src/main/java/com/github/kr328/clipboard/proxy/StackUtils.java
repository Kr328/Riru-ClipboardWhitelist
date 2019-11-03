package com.github.kr328.clipboard.proxy;

public class StackUtils {
    public static boolean hasMethodOnStack(Thread thread, String method) {
        StackTraceElement[] elements = thread.getStackTrace();

        for (int i = elements.length - 1; i >= 0; i--) {
            if (elements[i].getMethodName().equals(method))
                return true;
        }

        return false;
    }
}
