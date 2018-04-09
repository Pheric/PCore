package me.pheric.pcore.game.events;

import me.pheric.pcore.game.Game;

public class GameHaltedEvent extends StateEvent {
    public GameHaltedEvent(Game g) {
        super(g);
    }
}
