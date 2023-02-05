package com.github.kr328.clipboard;

import android.app.ActivityManagerHidden;
import android.app.ActivityThread;
import android.content.Context;
import android.os.Binder;
import android.os.UserHandleHidden;

import com.github.kr328.clipboard.shared.Constants;
import com.github.kr328.clipboard.shared.IClipboardWhitelist;
import com.github.kr328.clipboard.shared.Log;
import com.github.kr328.magic.util.BinderUtils;

public class WhitelistService extends IClipboardWhitelist.Stub {
    @Override
    public int version() {
        return Constants.SERVICE_VERSION;
    }

    @Override
    public String[] getAllExempted() {
        final int userId = UserHandleHidden.getUserId(Binder.getCallingUid());

        return DataStore.instance.getAllExempted(userId).toArray(new String[0]);
    }

    @Override
    public void addExempted(final String packageName) {
        final int userId = UserHandleHidden.getUserId(Binder.getCallingUid());

        DataStore.instance.addExempted(packageName, userId);

        forceStopPackage(packageName, userId);
    }

    @Override
    public void removeExempted(final String packageName) {
        final int userId = UserHandleHidden.getUserId(Binder.getCallingUid());

        DataStore.instance.removeExempted(packageName, userId);

        forceStopPackage(packageName, userId);
    }

    private void forceStopPackage(final String packageName, final int userId) {
        try {
            BinderUtils.withEvaluated(() -> {
                final ActivityThread thread = ActivityThread.currentActivityThread();
                if (thread == null) {
                    throw new IllegalStateException("System unavailable");
                }

                final Context context = thread.getSystemContext();
                if (context == null) {
                    throw new IllegalStateException("System unavailable");
                }

                final ActivityManagerHidden activityManager = context.getSystemService(ActivityManagerHidden.class);
                if (activityManager == null) {
                    throw new IllegalStateException("ActivityManager unavailable");
                }

                activityManager.forceStopPackageAsUser(packageName, userId);
            });
        } catch (final Exception e) {
            Log.w("Force stop package " + packageName + ": " + e, e);
        }
    }
}
