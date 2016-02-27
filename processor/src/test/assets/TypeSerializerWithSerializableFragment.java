package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.ArgsSerializer;
import com.sys1yagi.fragmentcreator.annotation.Args;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;
import com.sys1yagi.fragmentcreator.annotation.Serializer;

import java.io.Serializable;

import android.support.v4.app.Fragment;

@FragmentCreator
public class MainFragment extends Fragment {

    @Args
    @Serializer(to = Holder.class, serializer = SerializableSerializer.class)
    int id;

    public static class Holder implements Serializable {

        int id;

        public Holder(int id) {
            this.id = id;
        }
    }

    static class SerializableSerializer implements ArgsSerializer<Integer, Holder> {

        @Override
        public Holder serialize(Integer integer) {
            return new Holder(integer);
        }

        @Override
        public Integer deserialize(Holder s) {
            return s.id;
        }
    }
}
