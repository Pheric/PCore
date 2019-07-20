package me.pheric.pcore.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.plugin.PluginLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages and connects to a MySQL database
 *
 * @author StealthMajr (stealthmajr@gmail.com)
 * @see Connection
 * @see HikariDataSource
 * @see Result
 * @see DB
 * @since 0.1.0
 */
public class Database {

    private HikariDataSource dataSource;
    private Logger logger;

    /**
     * Initializes the class
     *
     * @param config the hikari config used for settings such as poolName, or the jdbcUrl!
     */
    public Database(HikariConfig config) {
        dataSource = new HikariDataSource(config);
        logger = Logger.getGlobal();
    }

    @Builder
    public Database(String name, String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariDataSource();
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setPoolName(name);

        // Configuration settings for the HikariDataSource
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);
        logger = Logger.getGlobal();
    }

    /**
     * Updates a database row with parameters
     *
     * @param query  The update statement
     * @param params The parameters to sanitize in the query
     * @return Returns -1 if there was an error, 0 if nothing was updated and vice-versa for 1
     */
    public int update(String query, Object... params) {
        // Try-resources block to automatically close the connection and prepared statement
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Loops through the parameters array and sets it on the prepared statement
            for (int i = 0; i < params.length; i++) {
                // Set the object in the array to the prepared statement
                // Which pretty much replaces the quotations
                // in MySQL queries based on index
                preparedStatement.setObject((i + 1), params[i]);
            }
            // Execute the update and return the value
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("An error occurred while attempting to perform this SQL statement!");
            logger.throwing("Database", "Database#update(String, Object...)", e);
            // Indicates an error
            return -1;
        }
    }

    /**
     * Find results with a MySQL query
     *
     * @param query  The query statement
     * @param params The parameters to sanitize in the query
     * @return A result object with all the data stored in it
     */
    public Result find(String query, Object... params) {
        // Try-resources block to automatically close the connection and prepared statement
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            // Loops through the parameters array and sets it on the prepared statement
            for (int i = 0; i < params.length; i++) {
                // Set the object in the array to the prepared statement
                // Which pretty much replaces the quotations
                // in MySQL queries based on index
                preparedStatement.setObject((i + 1), params[i]);
            }

            // Execute the query and grab the result set
            ResultSet resultSet = preparedStatement.executeQuery();
            // Initialize a new result based on
            Result result = new Result();
            result.setMetaData(resultSet.getMetaData());

            // Check if the result set has anymore data for us
            if (resultSet.next()) {
                // Loop through the columns
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    // Set the result to the object found in the MySQL column
                    result.updateObject(resultSet.getMetaData().getColumnName(i), resultSet.getObject((i)));
                }
            }

            return result;
        } catch (SQLException e) {
            logger.severe("An error occurred while attempting to perform this SQL statement!");
            logger.throwing("Database", "Database#find(String, Object...)", e);
            // Indicates an empty result
            return new Result();
        }
    }

    /**
     * Mirror of {@link #find(String, Object...)} for multiple results
     *
     * @param query  The query statement
     * @param params The parameters to sanitize in the query
     * @return An array of results with the data returned
     */
    public Collection<Result> findAll(String query, Object... params) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject((i + 1), params[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Result> resultList = new ArrayList<>();
            while (resultSet.next()) {
                Result result = new Result();
                result.setMetaData(resultSet.getMetaData());
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    result.updateObject(resultSet.getMetaData().getColumnName(i), resultSet.getObject((i)));
                }
                resultList.add(result);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return resultList;
        } catch (SQLException e) {
            logger.severe("An error occurred while attempting to perform this SQL statement!");
            logger.throwing("Database", "Database#findAll(String, Object...)", e);
            // Indicates an empty result
            return Collections.emptyList();
        }
    }

    /**
     * Grabs a new connection from the database pool
     *
     * @return The {@link Connection} linked to the {@link #dataSource}
     * @throws SQLException If the connection is closed, or any other error occurs
     */
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /**
     * Grabs a new connection from the database pool
     *
     * @return The {@link Optional<Connection>} linked to the {@link #dataSource}
     * @throws SQLException If the connection is closed, or any other error occurs
     */
    public Optional<Connection> getSafeConnection() {
        try {
            return Optional.ofNullable(getDataSource().getConnection());
        } catch (SQLException e) {
            logger.severe("An error occurred while attempting to grab the connection from the Hikari Pool!");
            logger.throwing("Database", "Database#getSafeConnection()", e);
            return Optional.empty();
        }
    }

    /**
     * Clean and dispose of all extra data floating about
     */
    public void dispose() {
        getDataSource().close();
    }

    /**
     * Grabs the {@link #dataSource} property to use
     *
     * @return The dataSource that returns connections for this database object
     */
    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
