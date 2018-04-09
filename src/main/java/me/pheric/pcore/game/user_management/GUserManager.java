package me.pheric.pcore.game.user_management;

import me.pheric.pcore.game.GameUser;
import org.bukkit.entity.Player;

import java.util.*;

public class GUserManager {
    private Map<UUID, GameUser> users = new HashMap<>();

    /**
     * Gets every registered {@link GameUser}
     *
     * @return Every registered GameUser
     */
    public Map<UUID, GameUser> getUsers() {
        return Collections.unmodifiableMap(users);
    }

    /**
     * Gets a specific {@link GameUser}
     *
     * @param uuid The {@link UUID} of the GameUser to retrieve
     * @return The {@link Optional} corresponding GameUser
     */
    public Optional<GameUser> getGameUser(UUID uuid) {
        return Optional.ofNullable(users.get(uuid));
    }

    /**
     * @see GUserManager#getGameUser(UUID)
     */
    public Optional<GameUser> getGameUser(Player p) {
        return getGameUser(p.getUniqueId());
    }

    /**
     * Adds/registers a {@link GameUser}
     *
     * @param gu The GameUser to add
     * @return Whether the GameUser already existed
     */
    public boolean addGameUser(GameUser gu) {
        if (users.containsValue(gu)) return false;
        users.put(gu.getId(), gu);
        return true;
    }

    /**
     * Removes a {@link GameUser}
     *
     * @param gu The GameUser to remove
     * @return Whether the GameUser existed in the first place
     */
    public boolean removeGameUser(GameUser gu) {
        if (users.containsValue(gu)) return false;
        users.remove(gu.getId());
        return true;
    }
}
