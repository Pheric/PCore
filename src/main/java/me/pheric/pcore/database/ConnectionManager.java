package me.pheric.pcore.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Database connections manager- this class creates a connection pool (a bunch of connections to a database that can be used concurrently, and can be returned to the pool for reuse).
 *
 * @author eric
 * @since 0.0.1-ALPHA
 */
public final class ConnectionManager {
    //                  NoSQL

    // Jedis
    private HashMap<String, Jedis> jedisMap = new HashMap<>();

    /**
     * Starts and sets the manager's {@link Jedis} instance
     *
     * @param id   The unique id of this connection
     * @param host Hostname for the server, default 'localhost'
     * @param port Port the server runs on
     * @return A new {@link Jedis} instance
     * @implNote This connection is <i>not</i> pooled!
     */
    public Jedis startJedisConnection(String id, String host, int port) {
        Jedis j = new Jedis(host, port);
        jedisMap.put(id, j);
        return j;
    }

    /**
     * @return A new {@link Jedis} instance
     * @see ConnectionManager#startJedisConnection(String, String, int)
     * {@code host} defaults to 'localhost'
     * {@code port} defaults to 6379
     */
    public Jedis startJedisConnection(String id) {
        return startJedisConnection(id, "localhost", 6379);
    }

    /**
     * Get the manager's {@link Jedis} instance
     *
     * @param id Unique id of the connection to retrieve
     * @return NULL or the {@link Jedis} instance
     * @see ConnectionManager#startJedisConnection(String, String, int)
     */
    public Jedis getJedisConnection(String id) {
        return jedisMap.get(id);
    }

    //                  SQL

    // Supported systems
    public enum RDBMS {
        MYSQL("com.mysql.jdbc.Driver"), POSTGRESQL("org.postgresql.Driver");

        String driver;

        RDBMS(String driver) {
            this.driver = driver;
        }
    }

    // HikariCP
    private HashMap<String, HikariDataSource> cpSources = new HashMap<>();

    /**
     * Initializes a SQL database connection pool
     *
     * @param username    Database account username
     * @param password    Database account password
     * @param hostname    Database daemon hostname
     * @param port        Database daemon port
     * @param database    The database to connect to
     * @param maxPoolSize Maximum number of connections to the database to keep alive
     * @return Success; unsuccessful on exception or if the pool already exists
     */
    public boolean initializeDatabasePool(String poolID, RDBMS system, String username, String password, String hostname, int port, String database, int maxPoolSize) {
        if (cpSources.containsKey(poolID)) return false;

        HikariConfig conf = new HikariConfig();
        try {
            Class.forName(system.driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
            // Cry
        }
        conf.setDriverClassName(system.driver);
        conf.setJdbcUrl("jdbc:" + system.name().toLowerCase() + "://" + hostname + ":" + port + "/" + database);
        conf.setUsername(username);
        conf.setPassword(password);
        conf.addDataSourceProperty("connectionTimeout", 30000);
        conf.addDataSourceProperty("idleTimeout", 60000);
        conf.addDataSourceProperty("maxLifetime", 90000); // TODO: Change; https://github.com/brettwooldridge/HikariCP#user-content-configuration-knobs-baby
        conf.addDataSourceProperty("minimumIdle", Math.round(maxPoolSize / 4)); // TODO: Tweak
        conf.addDataSourceProperty("maximumPoolSize", maxPoolSize);
        conf.addDataSourceProperty("poolName", poolID);
        cpSources.put(poolID, new HikariDataSource(conf));
        return true;
    }

    /**
     * Removes and kills a database connection pool
     *
     * @param poolID Pool to remove
     */
    public void removeDatabasePool(String poolID) {
        if (!cpSources.containsKey(poolID)) {
            Bukkit.getLogger().log(Level.INFO, "database pool not removed: no pool with id " + poolID + " found");
        } else {
            cpSources.get(poolID).close();
            cpSources.remove(poolID);
        }

        if (!dbiMap.containsKey(poolID)) {
            Bukkit.getLogger().log(Level.INFO, "DBI not removed: no DBI with id " + poolID + " found");
        } else {
            dbiMap.remove(poolID); // FIXME: are these connections all closed?
        }
    }


    // JDBC
    private HashMap<String, DBI> dbiMap = new HashMap<>();

    /**
     * Get an available {@link Connection} from the indicated pool
     *
     * @param poolID Pool to get a connection from
     * @return An available connection
     */
    public Connection getPooledConnection(String poolID) {
        try {
            if (!cpSources.containsKey(poolID)) {
                throw new RuntimeException("getting pool " + poolID + " failed: no matching pool configured");
            }
            return cpSources.get(poolID).getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("getting pooled connection from " + poolID + " failed: no available connections");
        }
    }


    // JDBI

    /**
     * Get an available {@link Handle}
     *
     * @param poolID Pool to use when getting a {@link Handle}
     * @return An available {@link Handle}
     */
    public Handle getPooledHandle(String poolID) {
        if (!cpSources.containsKey(poolID)) {
            throw new RuntimeException("getting pool " + poolID + " failed: no matching pool configured");
        }

        if (!dbiMap.containsKey(poolID)) {
            dbiMap.put(poolID, new DBI(cpSources.get(poolID)));
        }
        return dbiMap.get(poolID).open();
    }
}
