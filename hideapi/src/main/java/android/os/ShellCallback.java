package android.os;

public class ShellCallback implements Parcelable {
    protected ShellCallback(Parcel in) {
        throw new IllegalArgumentException("Stub!");
    }

    public static final Creator<ShellCallback> CREATOR = new Creator<ShellCallback>() {
        @Override
        public ShellCallback createFromParcel(Parcel in) {
            throw new IllegalArgumentException("Stub!");
        }

        @Override
        public ShellCallback[] newArray(int size) {
            throw new IllegalArgumentException("Stub!");
        }
    };

    @Override
    public int describeContents() {
        throw new IllegalArgumentException("Stub!");
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        throw new IllegalArgumentException("Stub!");
    }
}
