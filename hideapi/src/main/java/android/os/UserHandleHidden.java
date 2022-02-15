package android.os;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(UserHandle.class)
public class UserHandleHidden {
    public static final int USER_SYSTEM = 0;

    public static int getUserId(int uid) {
        throw new IllegalArgumentException("Stub!");
    }
}
