package me.pheric.pcore.game.user_management.teams;

import me.pheric.pcore.game.user_management.teams.events.TeamJoinEvent;
import me.pheric.pcore.game.user_management.teams.events.TeamLeaveEvent;
import me.pheric.pcore.game.user_management.teams.events.TeamScoreAboveThresholdEvent;
import me.pheric.pcore.game.user_management.teams.events.TeamSizeBelowThresholdEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Allows for teams to be more easily implemented.
 *
 * @author eric
 * @since 1.0.0-SNAPSHOT
 */
public class Team {
    private String teamName;
    private TeamColor teamColor;
    private int minPlayersThreshold = -1;
    private int maxPlayers;
    private long maxScoreThreshold = Long.MAX_VALUE;
    private int scoreMultiplier = 1;
    private boolean hidden = false;

    private List<Player> players = new ArrayList<>();
    private long score = 0;

    public Team(String teamName, TeamColor teamColor, int maxTeamSize) {
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.maxPlayers = maxTeamSize;
    }

    /**
     * Increment the team's score. If it exceeds the max threshold, {@link TeamScoreAboveThresholdEvent} will be internally called.
     *
     * @param increment Raw points to add. Will be internally multiplied.
     * @return The new score
     * @see Team#getScoreMultiplier()
     */
    public long incrScore(int increment) {
        score += scoreMultiplier * increment;

        if (score >= maxScoreThreshold) {
            Bukkit.getPluginManager().callEvent(new TeamScoreAboveThresholdEvent(this));
        }
        return score;
    }

    /**
     * Set the max threshold for the score. The score can exceed this value, but when it does, the {@link TeamScoreAboveThresholdEvent} will be called internally.
     *
     * @param max Max threshold to reach before the event is called.
     */
    public void setMaxScoreThreshold(long max) {
        maxScoreThreshold = max;
    }

    /**
     * Sets the score multiplier without any further changes.
     *
     * @param m The new multiplier value.
     */
    public void setScoreMultiplier(int m) {
        scoreMultiplier = m;
    }

    /**
     * Takes the current score, breaks it down, and re-multiplies it with the new multiplier, and sets the new multiplier. Not recommended if the multiplier is changed through {@link Team#setScoreMultiplier(int)}
     *
     * @param newMultiplier New multiplier to set and update the score by.
     */
    public void resetScoreWithNewMultiplier(int newMultiplier) {
        score /= scoreMultiplier;
        score *= newMultiplier;
        scoreMultiplier = newMultiplier;
    }

    /**
     * Set the minimum player amount threshold. When reached, the {@link TeamSizeBelowThresholdEvent} will be called internally.
     *
     * @param threshold The new threshold.
     */
    public void setMinPlayersThreshold(int threshold) {
        minPlayersThreshold = threshold;
    }

    /**
     * Adds a player if they aren't already in this team. Calls {@link TeamJoinEvent}
     *
     * @param p The player to add.
     * @return success
     */
    public boolean addPlayer(Player p) {
        Bukkit.getPluginManager().callEvent(new TeamJoinEvent(p, this));
        players.add(p);
        return true;
    }

    /**
     * Overload, see original. Adds a parameter to override size limits and hidden status (if applicable).
     *
     * @param p The player to add.
     * @return success
     * @see Team#addPlayer(Player)
     */
    public boolean addPlayer(Player p, boolean ovr) {
        if (ovr) {
            return addPlayer(p);
        } else if (players.size() < maxPlayers && !isHidden()) {
            return addPlayer(p);
        }

        return false;
    }

    /**
     * Removes a player if possible, and calls {@link TeamSizeBelowThresholdEvent} if the new team size is less than the minimum threshold. Calls {@link TeamLeaveEvent} if successful.
     *
     * @param p The player to remove.
     * @see Team#setMinPlayersThreshold(int)
     * @return success
     */
    public boolean removePlayer(Player p) {
        boolean ret = players.remove(p);

        if (ret) {
            Bukkit.getPluginManager().callEvent(new TeamLeaveEvent(p, this));

            if (players.size() <= minPlayersThreshold)
                Bukkit.getPluginManager().callEvent(new TeamSizeBelowThresholdEvent(this, p));
        }

        return ret;
    }

    /**
     * Overload. See original. Normally, this function would fail if the Team is hidden.
     * This function adds a parameter allowing the caller to disable this check.
     *
     * @param p The player to remove.
     * @param ovr Whether to ignore the hidden status (if applicable)
     * @see Team#setMinPlayersThreshold(int)
     * @see Team#removePlayer(Player)
     * @return success
     */
    public boolean removePlayer(Player p, boolean ovr) {
        if (isHidden() && !ovr) return false;

        return removePlayer(p);
    }

    /**
     * Sets all players matching the supplied {@link Predicate<Player>} to the Team's colored leather armor.
     *
     * @param matchAgainst The predicate to check players against.
     */
    public void setMatchedPlayersToColoredArmor(Predicate<Player> matchAgainst) {
        players.stream().filter(matchAgainst).forEach(p -> {
            ItemStack helm = getColoredMaterial(Material.LEATHER_HELMET),
                    chest = getColoredMaterial(Material.LEATHER_CHESTPLATE),
                    legg = getColoredMaterial(Material.LEATHER_LEGGINGS),
                    boots = getColoredMaterial(Material.LEATHER_BOOTS);
            PlayerInventory i = p.getInventory();
            i.setHelmet(helm);
            i.setChestplate(chest);
            i.setLeggings(legg);
            i.setBoots(boots);

            p.updateInventory();
        });
    }

    /**
     * Gets the team's current multiplied score.
     *
     * @return Current score
     */
    public long getScore() {
        return score;
    }

    /**
     * Gets all current players in the team
     *
     * @return All players in the team
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the minimum number of players before the {@link TeamSizeBelowThresholdEvent} is called.
     *
     * @return The minimum number of players
     */
    public int getMinPlayersThreshold() {
        return minPlayersThreshold;
    }

    /**
     * Gets the max size of this team
     *
     * @return max number of players in this team
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Whether the team size equals or exceeds the maximum team size
     *
     * @return whether the team size equals or exceeds the maximum team size
     */
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    /**
     * Whether the team size meets the minimum size
     *
     * @return whether the team size meets the minimum size
     */
    public boolean isAboveMinimums() {
        return players.size() >= minPlayersThreshold;
    }

    /**
     * Gets the max score threshold
     *
     * @return max score threshold
     */
    public long getMaxScoreThreshold() {
        return maxScoreThreshold;
    }

    /**
     * Gets the team name
     *
     * @return The team name
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * Gets the {@link TeamColor} for this team.
     *
     * @return The TeamColor for this team.
     */
    public TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * Gets the current score multiplier.
     *
     * @return The current score multiplier
     */
    public int getScoreMultiplier() {
        return scoreMultiplier;
    }

    /**
     * Whether the Team is hidden (players will not automatically join or leave this team, and it will be hidden from players, but not events)
     *
     * @return status
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets whether the Team is hidden
     *
     * @param disabled whether the Team is hidden
     * @return updated state
     */
    public boolean setHidden(boolean disabled) {
        return this.hidden = disabled;
    }

    private ItemStack getColoredMaterial(Material material) {
        ItemStack i = new ItemStack(material);
        LeatherArmorMeta m = (LeatherArmorMeta) i.getItemMeta(); // Unchecked, don't mess up
        m.setColor(teamColor.getTeamArmorColor());
        i.setItemMeta(m);
        return i;
    }
}
