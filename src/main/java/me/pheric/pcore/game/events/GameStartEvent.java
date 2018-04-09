package me.pheric.pcore.game.events;

import me.pheric.pcore.game.Game;

public class GameStartEvent extends StateEvent {
    public GameStartEvent(Game g) {
        super(g);
    }
}
