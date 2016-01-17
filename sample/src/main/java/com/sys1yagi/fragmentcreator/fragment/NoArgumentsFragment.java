package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.R;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@FragmentCreator
public class NoArgumentsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoArgumentsFragmentCreator.read(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no, container, false);
    }
}
