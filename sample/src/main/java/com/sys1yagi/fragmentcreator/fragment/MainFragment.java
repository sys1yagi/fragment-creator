package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.R;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;
import com.sys1yagi.fragmentcreator.log.Logger;
import com.sys1yagi.fragmentcreator.model.Shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@FragmentCreator
public class MainFragment extends Fragment {

    @Args
    String keyword;

    @Args(require = false)
    String userId;

    @Args
    Shop shop;

    @Args(require = false)
    Logger logger;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainFragmentCreator.read(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.keyword)).setText(keyword);
        ((TextView) view.findViewById(R.id.user_id)).setText(userId);
        ((TextView) view.findViewById(R.id.shop)).setText(shop.getId() + " " + shop.getName());
    }
}
