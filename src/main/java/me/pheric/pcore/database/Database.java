package me.pheric.pcore.database;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    /**
     * Initializes the class
     *
     * @param jdbcUrl  The JDBC URL for connecting, usually in the following format:
     *                 jdbc:mysql://hostname:port/database
     * @param username The username of this MySQL connection
     * @param password The password of this MySQL connection
     */
    public Database(String jdbcUrl, String username, String password) {
        dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setPoolName("Core");

        // Configuration settings for the HikariDataSource
        dataSource.setMaximumPoolSize(10);
    }

    /**
     * Updates a database row with parameters
     *
     * @param query  The update statement
     * @param params The parameters to sanitize in the query
     * @return Returns -1 if there was an error, 0 if nothing was updated and vice-versa for 1
     */
    int update(String query, Object... params) {
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
    Result find(String query, Object... params) {
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

            // Check if the result set has anymore data for us
            if (resultSet.next()) {
                // Loop through the columns
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    // Set the result to the object found in the MySQL column
                    result.set(resultSet.getMetaData().getColumnName(i), resultSet.getObject((i)));
                }
            }

            return result;
        } catch (SQLException e) {
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
    Collection<Result> findAll(String query, Object... params) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject((i + 1), params[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Result> resultList = new ArrayList<>();
            while (resultSet.next()) {
                Result result = new Result();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    result.set(resultSet.getMetaData().getColumnName(i), resultSet.getObject((i)));
                }
                resultList.add(result);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return resultList;
        } catch (SQLException e) {
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
    private Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /**
     * Clean and dispose of all extra data floating about
     */
    void dispose() {
        getDataSource().close();
    }

    /**
     * Grabs the {@link #dataSource} property to use
     *
     * @return The dataSource that returns connections for this database object
     */
    private HikariDataSource getDataSource() {
        return dataSource;
    }
}