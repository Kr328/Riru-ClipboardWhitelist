package com.android.internal.telephony;

import android.os.Binder;
import android.os.IBinder;

public interface ISub {
    public static abstract class Stub extends Binder implements ISub {
        public static ISub asInterface(IBinder binder) {
            throw new IllegalArgumentException("Stub!");
        }
    }


}
