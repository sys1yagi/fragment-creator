package com.sys1yagi.fragmentcreator.tool;

import com.sys1yagi.fragmentcreator.ArgsSerializer;
import com.sys1yagi.fragmentcreator.model.Product;

import android.os.Parcel;
import android.os.Parcelable;

public class StringSerializer implements ArgsSerializer<Product, String> {

    @Override
    public String serialize(Product product) {
        return product.getId() + "," + product.getName();
    }

    @Override
    public Product deserialize(String str) {
        String[] split = str.split(",");
        Product product = new Product();
        product.setId(Integer.parseInt(split[0]));
        product.setName(split[1]);
        return product;
    }
}
