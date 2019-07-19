package me.pheric.pcore.game.user_management.teams;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TeamManager {
    private List<Team> teams = new ArrayList<>();
    private int maxTeamSize, minTeamSize;
    private long maxScoreThreshold;

    public TeamManager(int maxTeamSize, int minTeamSizeThreshold, long maxScoreThreshold) {
        this.maxTeamSize = maxTeamSize;
        this.minTeamSize = minTeamSizeThreshold;
        this.maxScoreThreshold = maxScoreThreshold;
    }

    /**
     * Registers a {@link Team}
     *
     * @param team The Team to register
     * @return Whether it was previously registered
     */
    public boolean registerTeam(Team team) {
        return teams.add(team);
    }

    /**
     * Creates a basic {@link Team} and registers it
     *
     * @param teamName The name of the team.
     * @param tc       The {@link TeamColor} for the team.
     * @return Whether it was previously registered
     */
    public boolean registerTeam(String teamName, TeamColor tc) {
        if (isTeamRegistered(teamName)) {
            return false;
        }
        Team t = new Team(teamName, tc, maxTeamSize);
        t.setMaxScoreThreshold(maxScoreThreshold);
        t.setMinPlayersThreshold(minTeamSize);

        return registerTeam(t);
    }

    /**
     * Automatically adds a Player to the smallest team, if possible.
     *
     * @param p The Player to add
     * @return Success
     */
    public boolean autoAddPlayer(Player p) {
        Team lowest = null;
        for (Team t : teams) {
            if (t.isSizeLocked()) continue;

            if (t.getPlayers().size() < t.getMaxPlayers()) {
                if (lowest == null || t.getPlayers().size() < lowest.getPlayers().size()) {
                    lowest = t;
                }
            }
        }
        if (lowest == null) return false;
        lowest.addPlayer(p);

        return true;
    }

    /**
     * Gets the Team of a Player, if possible.
     *
     * @param p The Player whose team is needed
     * @return The {@link Optional} of the Team the player is in
     */
    public Optional<Team> getPlayerTeam(Player p) {
        return teams.stream().filter(t -> t.getPlayers().contains(p)).findFirst();
    }

    /**
     * Gets an {@link Optional} Team corresponding to the supplied name, if possible.
     *
     * @param teamName The name of the Team
     * @return The Team, if existent
     */
    public Optional<Team> getTeam(String teamName) {
        return teams.stream().filter(t -> t.getTeamName().equals(teamName)).findFirst();
    }

    /**
     * Gets the maximum number of Players per team.
     *
     * @return The max number of players in a team.
     */
    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    /**
     * Gets the minimum number of players per team.
     *
     * @return The minimum number of players in a team.
     */
    public int getMinTeamSize() {
        return minTeamSize;
    }

    /**
     * Checks whether the Team is registered.
     *
     * @param team The Team to check
     * @return Whether the Team is registered.
     */
    public boolean isTeamRegistered(Team team) {
        return teams.contains(team);
    }

    /**
     * @see TeamManager#isTeamRegistered(Team)
     */
    public boolean isTeamRegistered(String teamName) {
        return teams.stream().anyMatch(t -> t.getTeamName().equals(teamName));
    }

    /**
     * Gets all registered Teams.
     *
     * @return All registered Teams
     */
    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }
}
