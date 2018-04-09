package me.pheric.pcore.game.events;

import me.pheric.pcore.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Game game;

    public StateEvent(Game g) {
        game = g;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
