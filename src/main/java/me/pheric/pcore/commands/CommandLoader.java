package me.pheric.pcore.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2017, Photon156 (UUID: 1a088475-673c-46c9-9c6a-b266d5fe5435). All Rights Reserved.
 */
public final class CommandLoader {
    private Map<String, Cmd> commands = new HashMap<>();

    /**
     * Reads each class associated with every object, and loads the commands for each one.
     *
     * @param parserManager The (custom) {@link ComplexArgumentParserManager} to use on all command methods
     * @param objects       Instantiated classes containing annotated command methods
     */
    public void loadCommands(ComplexArgumentParserManager parserManager, Object... objects) {
        for (Object o : objects) {
            Class clazz = o.getClass();
            Arrays.stream(clazz.getDeclaredMethods()).filter(m -> m.getAnnotation(Command.class) != null).forEach(method -> commands.put(method.getName(), new Cmd(parserManager, method, o)));
        }
    }

    /**
     * @param objects Instantiated classes containing annotated command methods
     * @see CommandLoader#loadCommands(Object...)
     */
    // Overload
    public void loadCommands(Object... objects) {
        loadCommands(new ComplexArgumentParserManager(), objects);
    }

    public Map<String, Cmd> getRegisteredCommands() {
        return commands;
    }
}
