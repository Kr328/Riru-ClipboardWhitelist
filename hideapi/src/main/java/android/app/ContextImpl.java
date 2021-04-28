package android.app;

import android.content.Context;
import android.content.ContextWrapper;

class ContextImpl extends ContextWrapper {
    public ContextImpl(Context base) {
        super(base);
    }
}
