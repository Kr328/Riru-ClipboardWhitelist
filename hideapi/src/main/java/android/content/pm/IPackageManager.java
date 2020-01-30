package android.content.pm;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IPackageManager extends IInterface {
    abstract class Stub extends Binder implements IPackageManager {
        @Override
        public IBinder asBinder() {
            throw new IllegalArgumentException("Stub!");
        }

        public static IPackageManager asInterface(IBinder binder) {
            throw new IllegalArgumentException("Stub!");
        }
    }

    int getPackageUid(String packageName, int flags, int userId) throws RemoteException;
}
