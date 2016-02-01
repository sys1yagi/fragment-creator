package com.sys1yagi.fragmentcreator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Args {

    boolean require() default true;

    short defaultShort() default 0;

    long defaultLong() default 0;

    float defaultFloat() default 0;

    String defaultString() default "";

    char defaultChar() default 0;

    double defaultDouble() default 0;

    boolean defaultBoolean() default false;

    byte defaultByte() default 0;

    int defaultInt() default 0;
}
