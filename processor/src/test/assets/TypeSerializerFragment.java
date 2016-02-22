package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.ArgsSerializer;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;
import com.sys1yagi.fragmentcreator.annotation.Serializer;

import android.support.v4.app.Fragment;

@FragmentCreator
public class TypeSerializerFragment2 extends Fragment {

    @Args
    @Serializer(to = String.class, serializer = StringSerializer.class)
    int product;

    static class StringSerializer implements ArgsSerializer<Integer, String> {

        @Override
        public String serialize(Integer integer) {
            return integer.toString();
        }

        @Override
        public Integer deserialize(String s) {
            return Integer.parseInt(s);
        }
    }
}
