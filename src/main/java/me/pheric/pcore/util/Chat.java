package me.pheric.pcore.util;

import jdk.internal.joptsimple.internal.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Chat util
 *
 * @author Eric
 * @since 1.2.1
 */
public final class Chat {
    private Map<String, String> prefixes;
    private String keywordPrefix;

    /**
     * Sets up Chat
     *
     * @param keywordPrefix The prefix to precede every variable with; '$' is recommended
     */
    public Chat(String keywordPrefix) {
        prefixes = new HashMap<>();
        this.keywordPrefix = keywordPrefix;
    }

    /**
     * Colors a message (but does not replace variables!)
     *
     * @param text The text to color
     * @return The colored text
     */
    public static String color(String text) {
        return text = text.replace('&', '§');
    }

    // Overload
    public static List<String> color(String... text) {
        List<String> ret = new ArrayList<>();
        for (String line : text) {
            line = color(line);
            ret.addAll(Arrays.asList(line.split("\n")));
        }
        return ret;
    }

    // Overload
    public static List<String> color(List<String> arg) {
        return color((String[])arg.toArray());
    }

    /**
     * Adds a prefix / variable
     *
     * @param key   The variable name
     * @param value The valuse the variable will be replaced with in the String
     */
    public void addPrefix(String key, String value) {
        prefixes.put(key, value);
    }

    /**
     * Processes a line (replaces all variables & colors input)
     *
     * @param msg The message to process
     * @return The processed message
     */
    public String[] process(String... msg) {
        List<String> ret = new ArrayList<>();
        for (String line : msg) {
            ret.add(process(line));
        }
        return (String[]) ret.toArray();
    }

    /**
     * Overload: Processes a line (replaces all variables & colors input)
     *
     * @param msg The message to process
     * @return The processed message
     */
    public String process(String msg) {
        for (String prefix : prefixes.keySet()) {
            msg = msg.replace(keywordPrefix.concat(prefix), prefixes.get(prefix));
        }
        return color(msg);
    }

    /**
     * Sends a message to a {@link UUID}
     *
     * @param uuid Player to send the message to
     * @param msg  Message to send
     * @return Successful
     */
    public boolean sendMessage(UUID uuid, String... msg) {
        Player tgt;
        try {
            tgt = Bukkit.getPlayer(uuid);
            for (String line : msg) {
                tgt.sendMessage(process(line));
            }
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    // Overload
    public boolean sendMessage(Player tgt, String... msg) {
        return sendMessage(tgt.getUniqueId(), msg); // I know this is stupid.
    }

    // Overload
    public boolean sendMessage(CommandSender sender, String... msg) {
        if (sender == null) return false;

        if (sender instanceof Player) {
            return sendMessage((Player)sender, msg);
        } else {
            for (String line : msg) sender.sendMessage(process(line));
        }
        return true;
    }

    /**
     * Sends a message to a group of players
     *
     * @param tgts A {@link List<Player>} of players to send the message to
     * @param msg  Message to send
     */
    public void broadcastToGroup(List<Player> tgts, String... msg) {
        for (Player tgt : tgts) {
            sendMessage(tgt, msg);
        }
    }

    /**
     * Broadcasts a given message to every player on the server
     *
     * @param msg Message to send
     */
    public void broadcastMessage(String... msg) {
        Bukkit.getServer().getOnlinePlayers().forEach(p -> sendMessage(p, msg));
    }
}
