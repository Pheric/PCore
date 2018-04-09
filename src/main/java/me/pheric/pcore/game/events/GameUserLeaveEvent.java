package me.pheric.pcore.game.events;

import me.pheric.pcore.game.Game;
import me.pheric.pcore.game.GameUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameUserLeaveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private GameUser gameUser;

    public GameUserLeaveEvent(Game g, GameUser gu) {
        game = g;
        gameUser = gu;
    }

    public Game getGame() {
        return game;
    }

    public GameUser getGameUser() {
        return gameUser;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
