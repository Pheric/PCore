package me.pheric.pcore.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;

public class GameUser {
    public enum Mode {
        PLAYER, SPECTATOR
    }

    private UUID id;
    private boolean played = false;
    private int score;
    private Mode mode = Mode.PLAYER;
    private Map<String, List<?>> data = new HashMap<>();

    public GameUser(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public boolean hasPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Map<String, List<?>> getData() {
        return data;
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(id));
    }

    /**
     * Makes the player a spectator: changes their {@link Mode}, {@link GameMode}, allows flight, and hides them from all other players; or makes them a normal player.
     *
     * @param spectator Whether to set them as a spectator or player.
     * @return Success (whether the {@link Player} exists)
     */
    public boolean setSpectator(boolean spectator) {
        Optional<Player> p = getPlayer();
        if (!p.isPresent()) return false;
        if (spectator) {
            mode = Mode.SPECTATOR;
            p.get().setGameMode(GameMode.ADVENTURE);
            p.get().getActivePotionEffects().forEach(e -> p.get().removePotionEffect(e.getType()));
            p.get().setHealth(20);
            p.get().setFireTicks(0);
            p.get().setAllowFlight(true);
            Bukkit.getOnlinePlayers().forEach(pl -> pl.hidePlayer(p.get()));
        } else {
            mode = Mode.PLAYER;
            p.get().setGameMode(GameMode.SURVIVAL);
            p.get().getActivePotionEffects().forEach(e -> p.get().removePotionEffect(e.getType()));
            p.get().setHealth(20);
            p.get().setFireTicks(0);
            p.get().setAllowFlight(false);
            Bukkit.getOnlinePlayers().forEach(pl -> pl.showPlayer(p.get()));
        }
        return true;
    }
}