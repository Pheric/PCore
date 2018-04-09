package me.pheric.pcore.game.user_management.teams.events;

import me.pheric.pcore.game.user_management.teams.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamScoreAboveThresholdEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Team team;

    public TeamScoreAboveThresholdEvent(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
