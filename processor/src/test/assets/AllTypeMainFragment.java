package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;

import android.app.Fragment;
import android.os.Parcelable;
import java.util.List;
import java.io.Serializable;
import android.os.Parcel;

@FragmentCreator
public class MainFragment extends Fragment {

    @Args
    String keyword;

    @Args
    boolean flag;

    @Args
    byte aByte;

    @Args
    char aChar;

    @Args
    short aShort;

    @Args
    int anInt;

    @Args
    long aLong;

    @Args
    float aFloat;

    @Args
    double aDouble;

    @Args
    CharSequence charSequence;

    @Args
    Parcelable parcelable;

    @Args
    List<Shop> parcelableList;

    @Args
    Serializable serializable;

    public static class Shop implements Parcelable {

        int id;

        String name;

        public Shop(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.name);
        }

        public Shop() {
        }

        protected Shop(Parcel in) {
            this.id = in.readInt();
            this.name = in.readString();
        }

        public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
            public Shop createFromParcel(Parcel source) {
                return new Shop(source);
            }

            public Shop[] newArray(int size) {
                return new Shop[size];
            }
        };
    }
}
