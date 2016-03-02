package com.sys1yagi.fragmentcreator.util;

public class StringUtils {
    public static String camelCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
