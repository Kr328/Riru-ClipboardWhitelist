package android.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;

class ContextImpl extends ContextWrapper {
    private PackageManager mPackageManager;

    public ContextImpl(Context base) {
        super(base);
    }
}
