package com.sys1yagi.fragmentcreator.util;

public class Pair<First, Second> {

    public First first;

    public Second second;

    public Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public static <First, Second> Pair<First, Second> create(First first, Second second) {
        return new Pair<>(first, second);
    }

    public static <First, Second> Pair<First, Second> empty() {
        return Pair.create(null, null);
    }
}
