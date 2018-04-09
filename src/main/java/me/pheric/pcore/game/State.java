package me.pheric.pcore.game;

import me.pheric.pcore.game.events.*;

public enum State {
    JOIN_WAIT(GameJoinWaitEvent.class), // Game unstarted, players are joining from HUB
    JOIN_CLOSED(GameJoinClosedEvent.class), // Limit reached or game manually started
    START_WAIT(GameStartWaitEvent.class), // Players are teleporting to the game and waiting for it to start
    START(GameStartEvent.class), // Game starting
    END_WAIT(GameEndWaitEvent.class), // Game ending, all game operations halted
    END(GameEndEvent.class), // Game over, send everyone back to (lobby | hub)
    HALT(GameHaltedEvent.class); // Manual halt. Send players to hub, run cleanup process

    private Class associatedEvent;

    State(Class c) {
        associatedEvent = c;
    }

    public Class getAssociatedEvent() {
        return associatedEvent;
    }
}
