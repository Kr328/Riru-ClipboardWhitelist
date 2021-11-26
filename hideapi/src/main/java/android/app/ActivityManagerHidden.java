package android.app;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(ActivityManager.class)
public class ActivityManagerHidden {
    public void forceStopPackageAsUser(String packageName, int userId) {
        throw new IllegalArgumentException("Stub!");
    }
}
