package com.sys1yagi.fragmentcreator;

public interface ArgsSerializer<From, To> {

    To serialize(From from);

    From deserialize(To to);
}
