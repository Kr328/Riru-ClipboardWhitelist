package com.github.kr328.clipboard;

import android.app.ActivityManagerHidden;
import android.app.ActivityThread;
import android.content.Context;
import android.os.UserManagerHidden;

import com.github.kr328.clipboard.shared.Constants;
import com.github.kr328.clipboard.shared.IClipboardWhitelist;
import com.github.kr328.clipboard.shared.Log;
import com.github.kr328.clipboard.util.BinderUtil;

public class WhitelistService extends IClipboardWhitelist.Stub {
    @Override
    public int version() {
        return Constants.SERVICE_VERSION;
    }

    @Override
    public String[] queryPackages() {
        return DataStore.instance.queryPackages().toArray(new String[0]);
    }

    @Override
    public void addPackage(String packageName) {
        DataStore.instance.addPackage(packageName);

        forceStopPackage(packageName);
    }

    @Override
    public void removePackage(String packageName) {
        DataStore.instance.removePackage(packageName);

        forceStopPackage(packageName);
    }

    private void forceStopPackage(String packageName) {
        try {
            BinderUtil.withEvaluated(() -> {
                final ActivityThread thread = ActivityThread.currentActivityThread();
                if (thread == null)
                    throw new IllegalStateException("System unavailable");

                final Context context = thread.getSystemContext();
                if (context == null)
                    throw new IllegalStateException("System unavailable");

                final UserManagerHidden userManager = context.getSystemService(UserManagerHidden.class);
                if (userManager == null)
                    throw new IllegalStateException("UserManager unavailable");

                final ActivityManagerHidden activityManager = context.getSystemService(ActivityManagerHidden.class);
                if (activityManager == null)
                    throw new IllegalStateException("ActivityManager unavailable");

                userManager.getUsers().forEach((u) -> activityManager.forceStopPackageAsUser(packageName, u.id));
            });
        } catch (Exception e) {
            Log.w("Force stop package " + packageName + ": " + e, e);
        }
    }
}
