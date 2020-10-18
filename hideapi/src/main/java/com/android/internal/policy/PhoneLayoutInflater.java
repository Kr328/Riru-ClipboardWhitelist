package com.android.internal.policy;

import android.content.Context;
import android.view.LayoutInflater;

public class PhoneLayoutInflater extends LayoutInflater {
    protected PhoneLayoutInflater(Context context) {
        super(context);
    }

    protected PhoneLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    @Override
    public LayoutInflater cloneInContext(Context context) {
        throw new IllegalArgumentException("Stub!");
    }
}
