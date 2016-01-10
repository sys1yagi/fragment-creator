package com.sys1yagi.fragmentcreator;

import com.sys1yagi.fragmentcreator.exception.InvalidParameterException;
import com.sys1yagi.fragmentcreator.exception.UnsupportedTypeException;

import android.os.Bundle;

public class Arguments {

    Bundle argument;

    public Arguments(Bundle argument) {
        this.argument = argument;
    }

    public Bundle getArguments() {
        return argument;
    }

    public void checkRequire(Object param, String paramName) {
        if (param == null) {
            throw new UnsupportedTypeException(paramName + "is required.");
        }
    }

    public <T> void isValid(T param, Validator<T> validator) {
        if (!validator.isValid(param)) {
            //TODO rich message
            throw new InvalidParameterException(param.getClass().getName() + " is invalid.");
        }
    }
}
