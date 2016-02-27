package com.sys1yagi.fragmentcreator.tool;

import com.sys1yagi.fragmentcreator.ArgsSerializer;
import com.sys1yagi.fragmentcreator.model.Product;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableSerializer implements ArgsSerializer<Product, Parcelable> {

    @Override
    public Parcelable serialize(Product product) {
        return new Box(product.getId(), product.getName());
    }

    @Override
    public Product deserialize(Parcelable parcelable) {
        Box box = (Box) parcelable;
        Product product = new Product();
        product.setId(box.id);
        product.setName(box.name);
        return product;
    }

    static class Box implements Parcelable {

        int id;

        String name;

        public Box(int id, String name) {
            this.id = id;
            this.name = name;
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

        public Box() {
        }

        protected Box(Parcel in) {
            this.id = in.readInt();
            this.name = in.readString();
        }

        public static final Creator<Box> CREATOR = new Creator<Box>() {
            public Box createFromParcel(Parcel source) {
                return new Box(source);
            }

            public Box[] newArray(int size) {
                return new Box[size];
            }
        };
    }
}
