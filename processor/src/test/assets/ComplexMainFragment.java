package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;

import android.app.Fragment;

import java.io.Serializable;

@FragmentCreator
public class MainFragment extends Fragment {

    public static class Param implements Serializable {

    }

    @Args
    String keyword;

    @Args
    String userId;

    @Args(require = false)
    int recipeId;

    @Args(require = false)
    Param param;

    @Args(require = false)
    boolean isEdit;
}
