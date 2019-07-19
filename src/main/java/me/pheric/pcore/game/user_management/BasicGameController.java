package me.pheric.pcore.game.user_management;

import me.pheric.pcore.game.Game;
import me.pheric.pcore.game.GameChatFormat;
import me.pheric.pcore.game.GameUser;
import me.pheric.pcore.game.State;
import me.pheric.pcore.game.events.*;
import me.pheric.pcore.game.user_management.teams.Team;
import me.pheric.pcore.game.user_management.teams.events.TeamSizeBelowThresholdEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

import static me.pheric.pcore.game.State.JOIN_WAIT;

/**
 * Helps the {@link Game} progress through its {@link State} list, and helps with basic events like allowing players to join the game appropriately, saving them on death, etc.
 */
public class BasicGameController implements Listener {
    private JavaPlugin plugin;
    private Game game;
    private int minPlayersToStart;

    public BasicGameController(JavaPlugin plugin, Game g, int minPlayersToStart) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
        game = g;
        this.minPlayersToStart = minPlayersToStart;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String info = "";
        game.getUserManager().addGameUser(new GameUser(event.getPlayer().getUniqueId()));
        GameUser gu = game.getUserManager().getGameUser(event.getPlayer()).get(); // Better not be null, it's literally added on the immediately previous line..
        switch (game.getState()) {
            case JOIN_WAIT:
            case JOIN_CLOSED:
                if (game.getTeamManager().autoAddPlayer(event.getPlayer()) || game.getUserManager().getGameUser(event.getPlayer()).isPresent()) {
                    info = getTeamCountFormatted();
                } else {
                    event.getPlayer().kickPlayer("Game full!"); // TODO: Make this better
                }
                break;
            case END:
            case HALT:
                event.getPlayer().kickPlayer("The game has ended!");
                return;
            default:
                gu.setSpectator(true);
                info = " [Spectator]";
        }
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', GameChatFormat.JOIN.getFormat() + event.getPlayer().getDisplayName() + info));

        if (game.getTeamManager().getTeams().stream().allMatch(t -> t.isFull() && t.getPlayers().size() >= minPlayersToStart) && game.getState() == JOIN_WAIT) {
            game.setState(State.JOIN_CLOSED);
        }

        game.getUserManager().getUsers().values().stream().filter(u -> u.getMode() == GameUser.Mode.SPECTATOR && u.getPlayer().isPresent()).forEach(u -> event.getPlayer().hidePlayer(u.getPlayer().get())); // Keep spectators invisible
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Optional<GameUser> user = game.getUserManager().getGameUser(event.getPlayer());
        if (!user.isPresent()) return;
        game.getUserManager().removeGameUser(user.get());
        Bukkit.getPluginManager().callEvent(new GameUserLeaveEvent(game, user.get()));
        Bukkit.broadcastMessage(GameChatFormat.QUIT.getFormat() + event.getPlayer().getDisplayName());
    }

    @EventHandler
    public void onJoinWait(GameJoinWaitEvent event) {
        Bukkit.broadcastMessage(GameChatFormat.ANNOUNCEMENT.getFormat() + "Waiting for players to join...");
    }

    @EventHandler
    public void onJoinClosed(GameJoinClosedEvent event) {
        Bukkit.broadcastMessage(GameChatFormat.ANNOUNCEMENT.getFormat() + "Game starting in 20s!");

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(GameChatFormat.ANNOUNCEMENT.getFormat() + "Get ready!");
                game.setState(State.START_WAIT);
            }
        }.runTaskLater(plugin, 400);
    }

    @EventHandler
    public void onGameStartWait(GameStartWaitEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                game.setState(State.START);
            }
        }.runTaskLater(plugin, 100);
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        Bukkit.broadcastMessage(GameChatFormat.ANNOUNCEMENT.getFormat() + "Go!");
    }

    @EventHandler
    public void onGameEndWait(GameEndWaitEvent event) {
        Bukkit.broadcastMessage(GameChatFormat.ANNOUNCEMENT.getFormat() + "Game over!");

        game.getUserManager().getUsers().values().forEach(gu -> gu.setSpectator(true));
    }

    @EventHandler // Cancel all damage caused by / to spectators
    public void onDamage(EntityDamageByEntityEvent event) {
        Optional<GameUser> damager = Optional.empty(), damaged = Optional.empty();

        if (event.getEntity() instanceof Player) damaged = game.getUserManager().getGameUser((Player)event.getEntity());
        if (event.getDamager() instanceof Player) damager = game.getUserManager().getGameUser((Player)event.getDamager());

        if (damager.isPresent() && damager.get().getMode() == GameUser.Mode.SPECTATOR
                || damaged.isPresent() && damaged.get().getMode() == GameUser.Mode.SPECTATOR)
            event.setCancelled(true);
    }

    @EventHandler
    public void onTeamSizeTooLow(TeamSizeBelowThresholdEvent event) {
        if (game.getState() == State.JOIN_CLOSED) {
            game.setState(JOIN_WAIT);
        }
    }

    private String getTeamCountFormatted() {
        StringBuilder ret = new StringBuilder("&7(");
        boolean first = true;
        for (Team t : game.getTeamManager().getTeams()) {
            ret.append(String.format("&7%s&%s%c", first ? "" : "/", t.getTeamColor().getTeamChatColor().getChar(), t.isFull() ? '*' : t.getPlayers().size()));

            first = false;
        }
        ret.append("&7/&l").append(game.getTeamManager().getMinTeamSize()).append("&7)");
        return ret.toString();
    }
}
