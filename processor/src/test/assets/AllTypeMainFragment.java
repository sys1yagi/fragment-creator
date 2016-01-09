package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;

import android.app.Fragment;
import android.os.Parcelable;

import java.io.Serializable;

@FragmentCreator
public class MainFragment extends Fragment {

    @Args
    String keyword;

    @Args
    boolean flag;

    @Args
    byte aByte;

    @Args
    char aChar;

    @Args
    short aShort;

    @Args
    int anInt;

    @Args
    long aLong;

    @Args
    float aFloat;

    @Args
    double aDouble;

    @Args
    CharSequence charSequence;

    @Args
    Parcelable parcelable;

    @Args
    Serializable serializable;

}
