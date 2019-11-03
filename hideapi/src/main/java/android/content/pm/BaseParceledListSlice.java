package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

abstract class BaseParceledListSlice<T> implements Parcelable {
    public BaseParceledListSlice(List<T> list) {
        throw new IllegalArgumentException("Stub!");
    }

    public List<T> getList() {
        throw new IllegalArgumentException("Stub!");
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        throw new IllegalArgumentException("Stub!");
    }
}