package me.pheric.pcore.commands;

import jdk.internal.joptsimple.internal.Strings;
import me.pheric.pcore.util.Chat;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pheric.pcore.commands.Argument.Data.*;

/**
 * Custom {@link Command} implementation
 *
 * @author eric
 * @since 1.1.0
 */
final class Cmd extends org.bukkit.command.Command {
    private Method commandBody;
    private me.pheric.pcore.commands.Command cmdAnnotation;
    private ComplexArgumentParserManager parserManager;
    private Object callerInstance;
    private String err;

    Cmd(ComplexArgumentParserManager parserManager, Method body, Object instance) {
        super(body.getName());

        this.commandBody = body;
        this.cmdAnnotation = body.getAnnotation(me.pheric.pcore.commands.Command.class);
        this.parserManager = parserManager;
        this.callerInstance = instance;


        if (cmdAnnotation.names().length == 0) {
            this.setName(commandBody.getName());
        } else {
            this.setName(cmdAnnotation.names()[0]);

            List<String> aliases = new ArrayList<>();
            for (int i = 1; i < cmdAnnotation.names().length; i++) {
                aliases.add(cmdAnnotation.names()[i]);
            }

            this.setAliases(aliases);
        }

        err = "&cRunning command " + this.getName() + " failed: ";

        this.setUsage(cmdAnnotation.usage());

        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            CommandMap cm = (SimpleCommandMap) f.get(Bukkit.getServer());
            cm.register(this.getName(), this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        // Assert the sender is a player or console if allowed
        if (!(commandSender instanceof Player) && cmdAnnotation.playerOnly()) {
            commandSender.sendMessage(Chat.color(err + "this is a player only command!"));
            return true;
        }

        // Assert the sender has sufficient permissions
        if (!commandSender.hasPermission(cmdAnnotation.permission())) {
            commandSender.sendMessage(Chat.color(cmdAnnotation.noPerms()));
            return true;
        }


        AtomicInteger methodArguments = new AtomicInteger();
        Arrays.stream(commandBody.getParameters())
                .filter(p -> p.getAnnotation(Argument.class) != null && p.getAnnotation(Argument.class).dataType() == ARGUMENT)
                .forEach(param -> {
                    Class type = param.getType();
                    if (!parserManager.getParsers().containsKey(type)) {
                        throw new UnsupportedOperationException("Unsupported argument type " + param.getType() + "!");
                    }

                    methodArguments.addAndGet(parserManager.getParsers().get(type).getRequiredArguments());
                });
        if (methodArguments.get() > args.length) {
            commandSender.sendMessage(Chat.color(err + "too few arguments!"));
            commandSender.sendMessage(Chat.color("&cUsage: " + cmdAnnotation.usage()));
            return true;
        }

        Object[] invocationParameters = new Object[commandBody.getParameterCount()];
        int commandBodyParameterIndex = 0;
        //System.out.println(String.format("commandBody.getParameterCount() = %d; args.length = %d", commandBody.getParameterCount(), args.length));
        for (int commandArgumentsIndex = 0; commandBodyParameterIndex < commandBody.getParameterCount() && (commandArgumentsIndex < args.length || args.length == 0); ) {
            Parameter parameterAtCurrentIndex = commandBody.getParameters()[commandBodyParameterIndex];
            Argument parameterArgAnnotation = parameterAtCurrentIndex.getAnnotation(Argument.class);
            if (parameterArgAnnotation == null) {
                //System.out.println(String.format("parameterArgAnnotation[%d] is null. commandBodyParameterIndex++", commandBodyParameterIndex));
                commandBodyParameterIndex++; // Skip to next parameter, ignoring this one
                continue;
            }

            if (parameterArgAnnotation.dataType() == SENDER) {
                invocationParameters[commandBodyParameterIndex] = commandSender;
                commandBodyParameterIndex++;

            } else if (parameterArgAnnotation.dataType() == ARGUMENT) {
                ComplexArgumentParser cap = parserManager.getParsers().get(parameterAtCurrentIndex.getType());
                if (cap == null) {
                    throw new UnsupportedOperationException("Unsupported argument type " + parameterAtCurrentIndex.getType() + "!");
                }

                Object o = cap.parseInput((String[]) ArrayUtils.subarray(args, commandArgumentsIndex, commandArgumentsIndex + cap.getRequiredArguments()));
                if (o == null && !parameterArgAnnotation.nullable()) {
                    commandSender.sendMessage(Chat.color(err + parameterArgAnnotation.nullError()));
                    return true;
                }

                invocationParameters[commandBodyParameterIndex] = o;
                commandBodyParameterIndex++;
                commandArgumentsIndex += cap.getRequiredArguments();

            } else if (parameterArgAnnotation.dataType() == ARGSLICE) {
                if (parameterArgAnnotation.argSliceStart() > args.length - 1 && !parameterArgAnnotation.nullable()) {
                    commandSender.sendMessage(Chat.color(err + "too few arguments!"));
                    commandSender.sendMessage(Chat.color("&cUsage: " + cmdAnnotation.usage()));
                    return true;
                }

                if (parameterAtCurrentIndex.getType() != String[].class) {
                    throw new UnsupportedOperationException("ArgSlice parameter must be of type String[]!");
                }

                invocationParameters[commandBodyParameterIndex] = Arrays.copyOfRange(args, parameterArgAnnotation.argSliceStart(), args.length);
                commandBodyParameterIndex++;
            }

            //System.out.println(String.format("commandArgumentsIndex = %d; commandBodyParameterIndex = %d;", commandArgumentsIndex, commandBodyParameterIndex));
        }

        try {
            commandBody.invoke(callerInstance, invocationParameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return true;
    }
}
