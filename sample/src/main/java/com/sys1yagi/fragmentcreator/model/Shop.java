package com.sys1yagi.fragmentcreator.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Shop implements Parcelable {

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
