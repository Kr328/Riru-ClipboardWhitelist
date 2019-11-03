package android.os;

public class ServiceManagerNative extends Binder implements IServiceManager {
    public static IServiceManager asInterface(IBinder binder) {
        throw new IllegalArgumentException("Unsupported");
    }

    // Pie
    @Override
    public IBinder getService(String paramString) throws RemoteException {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public IBinder checkService(String name) throws RemoteException {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public void addService(String name, IBinder service, boolean allowIsolated, int dumpFlags) throws RemoteException {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public String[] listServices(int dumpFlags) throws RemoteException {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public void setPermissionController(IPermissionController controller) throws RemoteException {
        throw new IllegalArgumentException("Unsupported");
    }

    // Oreo
    @Override
    public void addService(String name, IBinder service, boolean allowIsolated) throws RemoteException {

    }

    @Override
    public String[] listServices() throws RemoteException {
        return new String[0];
    }

    @Override
    public IBinder asBinder() {
        throw new IllegalArgumentException("Unsupported");
    }
}
