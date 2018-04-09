package me.pheric.pcore.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright (c) 2017, Photon156 (UUID: 1a088475-673c-46c9-9c6a-b266d5fe5435). All Rights Reserved.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    /**
     * Nullable array of Strings: The first is the main command name, the rest are aliases; if null, the target method's name is the command name and there are no aliases
     *
     * @return Command name and aliases
     */
    String[] names();

    String permission() default "general.*";

    String noPerms() default "&cRunning the command failed: insufficient permission.";

    String usage() default "§cThere is no usage information.";

    boolean playerOnly() default true;
}
