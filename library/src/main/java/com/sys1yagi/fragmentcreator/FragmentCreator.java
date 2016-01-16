package com.sys1yagi.fragmentcreator;

import com.sys1yagi.fragmentcreator.exception.InvalidParameterException;
import com.sys1yagi.fragmentcreator.exception.UnsupportedTypeException;

import java.lang.reflect.Field;

public abstract class FragmentCreator {

    public static void checkRequire(Object param, String paramName) {
        if (param == null) {
            throw new UnsupportedTypeException(paramName + "is required.");
        }
    }

    public static <T> void isValid(T param, Validator<T> validator) {
        if (!validator.isValid(param)) {
            //TODO rich message
            throw new InvalidParameterException(param.getClass().getName() + " is invalid.");
        }
    }

    public static <T> void set(T instance, String fieldName, Object value) {
        try {
            Class<?> clazz = instance.getClass();
            Field field = clazz.getField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
            field.setAccessible(false);
        } catch (Exception e) {
            //no op
        }
    }

    public static <T> Object get(T instance, String fieldName) {
        try {
            Class<?> clazz = instance.getClass();
            Field field = clazz.getField(fieldName);
            field.setAccessible(true);
            Object value = field.get(instance);
            field.setAccessible(false);
            return value;
        } catch (Exception e) {
            //no op
        }
        return null;
    }
}
