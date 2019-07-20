package me.pheric.pcore.database;

import java.util.Collection;

/**
 * Manages a global database for ease of access
 *
 * @author StealthMajr (stealthmajr@gmail.com)
 * @see Result
 * @see DB
 * @since 0.1.0
 */
public class DB {

    private static Database database;

    /**
     * Find results with a MySQL query
     *
     * @param query  The query statement
     * @param params The parameters to sanitize in the query
     * @return A result object with all the data stored in it
     */
    public static Result find(String query, Object... params) {
        return database.find(query, params);
    }

    /**
     * Mirror of {@link #find(String, Object...)} for multiple results
     *
     * @param query  The query statement
     * @param params The parameters to sanitize in the query
     * @return An array of results with the data returned
     */
    public static Collection<Result> findAll(String query, Object... params) {
        return database.findAll(query, params);
    }

    /**
     * Updates a database row with parameters
     *
     * @param query  The update statement
     * @param params The parameters to sanitize in the query
     * @return Returns -1 if there was an error, 0 if nothing was updated and vice-versa for 1
     */
    public static int update(String query, Object... params) {
        return database.update(query, params);
    }

    // COMMON METHODS

    /**
     * Grabs the global database to run queries on
     *
     * @return The global {@link Database} instance
     */
    public static Database getGlobal() {
        return database;
    }

    /**
     * SHOULD NOT BE USED UNLESS YOU KNOW WHAT YOU'RE DOING.
     */
    public static void setGlobal(Database database) {
        DB.database = database;
    }

    /**
     * Clean and dispose of all extra data floating about
     *
     * @see Database#dispose()
     */
    public static void dispose() {
        DB.database.dispose();
    }
}
