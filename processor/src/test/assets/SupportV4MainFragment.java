package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;

import android.support.v4.app.Fragment;

@FragmentCreator
public class SupportV4MainFragment extends Fragment {

    @Args
    String keyword;

    @Args(require = false)
    String userId;
}
