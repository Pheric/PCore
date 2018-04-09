package me.pheric.pcore.game;

import org.bukkit.ChatColor;

public enum GameChatFormat {
    JOIN("&8Join> "),
    QUIT("&8Quit> "),
    ANNOUNCEMENT("&aGame> &7"),
    NOTICE("&cNotice> &f");

    private String format;

    GameChatFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return ChatColor.translateAlternateColorCodes('&', format);
    }
}
