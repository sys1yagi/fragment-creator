package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;

import android.app.Fragment;

@FragmentCreator
public class MainFragment extends Fragment {

    @Args
    String keyword;

    @Args
    String userId;
}
