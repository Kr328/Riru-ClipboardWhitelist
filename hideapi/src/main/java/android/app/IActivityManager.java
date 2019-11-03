package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;

public abstract interface IActivityManager extends IInterface {
    public static class Stub extends Binder implements IActivityManager {
        public static IActivityManager asInterface(IBinder binder) {
            throw new IllegalArgumentException("Unsupported");
        }

        @Override
        public IBinder asBinder() {
            throw new IllegalArgumentException("Unsupported");
        }
    }
}
