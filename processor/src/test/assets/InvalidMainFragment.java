package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;

import androidx.fragment.app.Fragment;

@FragmentCreator
public class InvalidMainFragment extends Activity {

    @Args
    String keyword;

    @Args(require = false)
    String userId;
    
}
