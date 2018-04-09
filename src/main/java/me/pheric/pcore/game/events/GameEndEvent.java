package me.pheric.pcore.game.events;

import me.pheric.pcore.game.Game;

public class GameEndEvent extends StateEvent {
    public GameEndEvent(Game g) {
        super(g);
    }
}
