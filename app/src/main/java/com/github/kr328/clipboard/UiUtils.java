package com.github.kr328.clipboard;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

public final class UiUtils {
    public static float getActionBarSize(Context context) {
        final TypedValue value = new TypedValue();

        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, value, true);

        return value.getDimension(context.getResources().getDisplayMetrics());
    }

    public static float getToolbarElevation(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.toolbarElevation);
    }

    public static void applySystemBarsTranslucent(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
        } else {
            final int flags = window.getDecorView().getSystemUiVisibility() |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            window.getDecorView().setSystemUiVisibility(flags);
        }

        window.getAttributes().layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
    }

    public static boolean isListViewAtTop(ListView view) {
        return view.getChildCount() == 0 || view.getChildAt(0).getTop() == view.getPaddingTop();
    }
}
