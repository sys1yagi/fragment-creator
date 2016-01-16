package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;

import android.app.Fragment;

import java.io.Serializable;

@FragmentCreator
public class MainFragment extends Fragment {

    interface A {

    }

    interface B {

    }

    interface C extends Serializable {

    }

    public static class Param implements Serializable, B {

    }

    public static class Param2 extends Param implements A {

    }

    @Args(require = false)
    Param2 param;

    @Args(require = false)
    C c;

}
