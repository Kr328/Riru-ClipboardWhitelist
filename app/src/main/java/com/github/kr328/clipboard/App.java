package com.github.kr328.clipboard;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public final class App implements Comparable<App> {
    private final Drawable icon;
    private final String packageName;
    private final String label;

    private boolean selected;

    public App(Drawable icon, String packageName, String label, boolean selected) {
        this.icon = icon;
        this.packageName = packageName;
        this.label = label;
        this.selected = selected;
    }

    public static App fromApplicationInfo(ApplicationInfo info, PackageManager packageManager, boolean selected) {
        return new App(info.loadIcon(packageManager), info.packageName, info.loadLabel(packageManager).toString(), selected);
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getLabel() {
        return label;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int compareTo(App o) {
        if (selected && o.selected)
            return label.compareTo(o.label);

        if (selected)
            return -1;

        if (o.selected)
            return 1;

        return label.compareTo(o.label);
    }
}
