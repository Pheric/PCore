package me.pheric.pcore.game.user_management.teams.events;

import me.pheric.pcore.game.user_management.teams.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Optional;

public class TeamSizeBelowThresholdEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Team team;
    private Optional<Player> lastToQuit;

    public TeamSizeBelowThresholdEvent(Team team, Player lastToLeave) {
        this.team = team;
        this.lastToQuit = Optional.ofNullable(lastToLeave);
    }

    public Optional<Player> getLastToQuit() {
        return lastToQuit;
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
