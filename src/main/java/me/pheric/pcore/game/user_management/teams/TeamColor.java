package me.pheric.pcore.game.user_management.teams;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public class TeamColor {
    private ChatColor teamChatColor;
    private Color teamArmorColor;

    public TeamColor(ChatColor teamChatColor, Color teamArmorColor) {
        this.teamChatColor = teamChatColor;
        this.teamArmorColor = teamArmorColor;
    }

    public boolean equals(TeamColor tc) {
        return tc.teamChatColor.equals(teamChatColor) && tc.teamArmorColor.equals(teamArmorColor);
    }

    public ChatColor getTeamChatColor() {
        return teamChatColor;
    }

    public Color getTeamArmorColor() {
        return teamArmorColor;
    }
}
