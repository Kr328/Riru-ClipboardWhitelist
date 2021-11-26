package android.content.pm;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(PackageManager.class)
public class PackageManagerHidden {
    public int getPackageUidAsUser(String packageName, int userId)
            throws PackageManager.NameNotFoundException {
        throw new IllegalArgumentException("Stub!");
    }
}
