package android.os;

public interface IServiceManager extends IInterface {
    // Pie
    IBinder getService(String name) throws RemoteException;

    IBinder checkService(String name) throws RemoteException;

    void addService(String name, IBinder service, boolean allowIsolated, int dumpFlags)
            throws RemoteException;

    String[] listServices(int dumpFlags) throws RemoteException;

    void setPermissionController(IPermissionController controller)
            throws RemoteException;

    // Oreo
    void addService(String name, IBinder service, boolean allowIsolated)
            throws RemoteException;

    String[] listServices() throws RemoteException;
}
