package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.R;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;
import com.sys1yagi.fragmentcreator.annotation.Serializer;
import com.sys1yagi.fragmentcreator.model.Product;
import com.sys1yagi.fragmentcreator.tool.ParcelableSerializer;
import com.sys1yagi.fragmentcreator.tool.StringSerializer;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@FragmentCreator
public class TypeSerializerFragment extends Fragment {

    @Args
    @Serializer(to = String.class, serializer = StringSerializer.class)
    Product product;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypeSerializerFragmentCreator.read(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_type_serializer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.id)).setText(String.valueOf(product.getId()));
        ((TextView) view.findViewById(R.id.name)).setText(product.getName());
    }
}
