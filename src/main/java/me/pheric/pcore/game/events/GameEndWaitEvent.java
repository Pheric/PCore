package me.pheric.pcore.game.events;

import me.pheric.pcore.game.Game;

public class GameEndWaitEvent extends StateEvent {
    public GameEndWaitEvent(Game g) {
        super(g);
    }
}
