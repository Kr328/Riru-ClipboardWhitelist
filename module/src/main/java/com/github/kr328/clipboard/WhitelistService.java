package com.github.kr328.clipboard;

import com.github.kr328.clipboard.shared.Constants;
import com.github.kr328.clipboard.shared.IClipboardWhitelist;

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
    }

    @Override
    public void removePackage(String packageName) {
        DataStore.instance.removePackage(packageName);
    }
}
