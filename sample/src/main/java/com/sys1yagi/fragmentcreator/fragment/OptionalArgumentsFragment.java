package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.R;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;
import com.sys1yagi.fragmentcreator.log.Logger;
import com.sys1yagi.fragmentcreator.model.Shop;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

@FragmentCreator
public class OptionalArgumentsFragment extends Fragment {

    @Args(require = false)
    long id;

    @Args(require = false)
    String keyword;

    @Args(require = false)
    Logger logger;

    @Args(require = false)
    List<Shop> shops;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OptionalArgumentsFragmentCreator.read(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_optional, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.id)).setText(Long.toString(id));
        ((TextView) view.findViewById(R.id.keyword)).setText(keyword);
        if (shops != null) {
            ((TextView) view.findViewById(R.id.shop_count)).setText(String.valueOf(shops.size()));
        }
    }
}
