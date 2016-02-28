package com.sys1yagi.fragmentcreator.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayListCreator {

    public static <T> ArrayList<T> create(List<T> source) {
        if (source == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(source);
    }
}
