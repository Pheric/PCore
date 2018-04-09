package me.pheric.pcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class ComplexArgumentParserManager {
    private Map<Class, ComplexArgumentParser> parsers = new HashMap<>();

    public ComplexArgumentParserManager() {
        // Load some nice defaults

        class PlayerParser extends ComplexArgumentParser {
            @Override
            public int getRequiredArguments() {
                return 1;
            }

            @Override
            public Object parseInput(String... args) {
                return Bukkit.getPlayer(args[0]);
            }
        }

        class IntegerParser extends ComplexArgumentParser {
            @Override
            public int getRequiredArguments() {
                return 1;
            }

            @Override
            public Object parseInput(String... args) {
                try {
                    return Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }

        class WorldParser extends ComplexArgumentParser {
            @Override
            public int getRequiredArguments() {
                return 1;
            }

            @Override
            public Object parseInput(String... args) {
                return Bukkit.getWorld(args[0]);
            }
        }

        class StringParser extends ComplexArgumentParser { // The most complex of them all
            @Override
            public int getRequiredArguments() {
                return 1;
            }

            @Override
            public Object parseInput(String... args) {
                return args[0];
            }
        }

        addParser(Player.class, new PlayerParser());
        addParser(Integer.class, new IntegerParser());
        addParser(World.class, new WorldParser());
        addParser(String.class, new StringParser());
    }

    public void addParser(Class clazz, ComplexArgumentParser parser) {
        parsers.put(clazz, parser);
    }

    public Map<Class, ComplexArgumentParser> getParsers() {
        return parsers;
    }
}
