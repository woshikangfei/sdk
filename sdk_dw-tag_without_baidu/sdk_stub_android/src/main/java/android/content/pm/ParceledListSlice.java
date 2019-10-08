package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ParceledListSlice<T extends Parcelable> implements Parcelable {
    protected ParceledListSlice(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        throw new RuntimeException("Stub!!!");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public List<T> getList() {
        throw new RuntimeException("Stub!!!");
    }

    public static final Creator<ParceledListSlice> CREATOR = new Creator<ParceledListSlice>() {
        @Override
        public ParceledListSlice createFromParcel(Parcel in) {
            return new ParceledListSlice(in);
        }

        @Override
        public ParceledListSlice[] newArray(int size) {
            return new ParceledListSlice[size];
        }
    };
}
