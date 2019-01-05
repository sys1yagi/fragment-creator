package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;

import androidx.fragment.app.Fragment;

@FragmentCreator
public class MainFragment extends Fragment {

    @Args(require = false)
    String keyword;

    @Args(require = false)
    String userId;
}
