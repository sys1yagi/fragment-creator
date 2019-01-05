package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;

@FragmentCreator
public class InvalidMainFragment extends Exception {

    @Args
    String keyword;

    @Args(require = false)
    String userId;
    
}
