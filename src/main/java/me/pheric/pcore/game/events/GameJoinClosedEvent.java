package me.pheric.pcore.game.events;

import me.pheric.pcore.game.Game;

public class GameJoinClosedEvent extends StateEvent {
    public GameJoinClosedEvent(Game g) {
        super(g);
    }
}
