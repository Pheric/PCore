package me.pheric.pcore.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright (c) 2017, Photon156 (UUID: 1a088475-673c-46c9-9c6a-b266d5fe5435). All Rights Reserved.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {
    Data dataType() default Data.ARGUMENT;

    boolean nullable();

    String nullError() default "&cError running command: an argument was unable to be parsed correctly or the dataType it points to was null.";

    int argSliceStart() default 0;

    enum Data {
        SENDER, ARGUMENT, ARGSLICE
    }
}
