package android.provider;

import android.content.ContentResolver;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(Settings.Secure.class)
public class SettingsSecure {
    public static String getStringForUser(ContentResolver resolver, String name, int userId) {
        throw new IllegalArgumentException("Stub!");
    }
}
