package me.pheric.pcore.game.user_management.teams.events;

import me.pheric.pcore.game.user_management.teams.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Optional;

public class TeamJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Team team;
    private Player p;

    public TeamJoinEvent(Player p, Team team) {
        this.team = team;
        this.p = p;
    }

    public Optional<Team> getTeam() {
        return Optional.ofNullable(team);
    }

    public Player getPlayer() {
        return p;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
