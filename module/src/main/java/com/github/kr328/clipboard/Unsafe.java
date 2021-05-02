package com.github.kr328.clipboard;

@SuppressWarnings("unchecked")
public final class Unsafe {
    public static <T> T unsafeCast(Object obj) {
        if (obj == null)
            return null;

        return (T) obj;
    }
}
