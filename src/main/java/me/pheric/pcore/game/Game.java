package me.pheric.pcore.game;

import me.pheric.pcore.game.events.StateEvent;
import me.pheric.pcore.game.user_management.BasicGameController;
import me.pheric.pcore.game.user_management.GUserManager;
import me.pheric.pcore.game.user_management.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Game {
    private State state = State.JOIN_WAIT;
    private TeamManager teamManager;
    private GUserManager userManager;

    Game(JavaPlugin plugin, int minPlayersToStart, int maxPlayers, TeamManager tm, GUserManager userManager) {
        this.userManager = userManager;
        teamManager = tm;

        // Initialize controller
        new BasicGameController(plugin, this, minPlayersToStart);
    }

    /**
     * Sets the Game's state, and automatically calls the associated {@link StateEvent} derivative
     *
     * @param newState The new {@link State} of the Game
     */
    public void setState(State newState) { // Aikar: use switch block, handle each case explicitly
        state = newState;

        try { // TODO: Fix this.. but I probably won't. ~Eric 12/15/17
            Constructor constructor = newState.getAssociatedEvent().getConstructor(Game.class);
            constructor.setAccessible(true);
            Bukkit.getPluginManager().callEvent((StateEvent) constructor.newInstance(this));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the current {@link State} of the Game
     *
     * @return State of the Game
     */
    public State getState() {
        return state;
    }


    /**
     * Gets the {@link TeamManager} for this Game
     *
     * @return The TeamManager for this game
     */
    public TeamManager getTeamManager() {
        return teamManager;
    }

    /**
     * Gets the {@link GUserManager} for this Game.
     *
     * @return the GUserManager for this Game.
     */
    public GUserManager getUserManager() {
        return userManager;
    }
}
