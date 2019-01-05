package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;

import androidx.fragment.app.Fragment;

@FragmentCreator
public class MainFragment extends Fragment {

    @Args(require = false, defaultString = "default")
    String userId;

    @Args(require = false, defaultChar = 'a')
    char aChar;
}
