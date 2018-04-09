package me.pheric.pcore.util;

import org.bukkit.ChatColor;

import java.util.regex.Pattern;

/**
 * Formats and parses input
 *
 * @author Eric
 * @since 1.0
 */
public final class Formatter {

    private static final Pattern IPPATTERN = Pattern.compile("\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b");
    private static final Pattern UUIDPATTERN = Pattern.compile("/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i");

    /**
     * Format a message with a prefix
     *
     * @param prefix  The prefix to format into the message
     * @param message The message to format
     * @return The formatted prefix
     */
    public static String format(String prefix, String message) {
        return ChatColor.BLUE + "[" + prefix + "] " + ChatColor.GRAY + message;
    }

    /**
     * Checks if argument is a valid IP address
     *
     * @param str The String to check
     * @return Valid IP
     */
    public static boolean isValidIPAddr(String str) {
        return IPPATTERN.matcher(str).matches();
    }

    /**
     * Checks if argument is a valid long UUID (contains dashes)
     *
     * @param str String to check
     * @return Whether the argument is a valid long UUID
     */
    public static boolean isLongUUID(String str) {
        return UUIDPATTERN.matcher(str).matches();
    }

    /**
     * Takes a String (short or long UUID) and formats it into a long UUID if possible.
     *
     * @param str The String to lengthen
     * @return Lengthened UUID
     */
    public static String formatUUID(String str) {
        if (str.length() != 32 && !str.contains("-")) {
            throw new IllegalArgumentException("Bad UUID string!");
        }

        if (isLongUUID(str)) return str;

        return str.substring(0, 8) + "-" + str.substring(10, 14) + "-" + str.substring(14, 18) + "-" + str.substring(18, 22) + "-" + str.substring(22, str.length());
    }
}
