package com.github.kr328.clipboard.shared;

interface IClipboardWhitelist {
    int version();

    String[] getAllExempted();
    void addExempted(String packageName);
    void removeExempted(String packageName);
}