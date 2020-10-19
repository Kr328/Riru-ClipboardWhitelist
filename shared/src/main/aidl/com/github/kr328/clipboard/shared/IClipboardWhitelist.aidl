package com.github.kr328.clipboard.shared;

interface IClipboardWhitelist {
    int version();

    String[] queryPackages();
    void addPackage(String packageName);
    void removePackage(String packageName);
}