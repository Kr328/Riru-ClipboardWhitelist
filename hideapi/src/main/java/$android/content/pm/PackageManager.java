package $android.content.pm;

public abstract class PackageManager {
    public abstract int getPackageUidAsUser(String packageName, int userId)
            throws android.content.pm.PackageManager.NameNotFoundException;
}
