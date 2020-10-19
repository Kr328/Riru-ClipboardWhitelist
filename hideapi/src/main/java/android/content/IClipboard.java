package android.content;

import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

public interface IClipboard {
    ClipData getPrimaryClip(String pkg, int userId) throws RemoteException;
    ClipDescription getPrimaryClipDescription(String callingPackage, int userId) throws RemoteException;
    boolean hasPrimaryClip(String callingPackage, int userId) throws RemoteException;
    boolean hasClipboardText(String callingPackage, int userId) throws RemoteException;
    void addPrimaryClipChangedListener(IOnPrimaryClipChangedListener listener,
                                       String callingPackage, int userId) throws RemoteException;
    void removePrimaryClipChangedListener(IOnPrimaryClipChangedListener listener,
                                          String callingPackage, int userId) throws RemoteException;

    abstract class Stub extends Binder implements IClipboard {
        public static IClipboard asInterface(IBinder binder) {
            throw new IllegalArgumentException("Stub!");
        }
    }
}