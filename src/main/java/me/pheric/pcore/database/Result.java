package me.pheric.pcore.database;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
class Result {

    private Map<String, Object> map = new HashMap<>();
    private ResultSetMetaData metaData;

    <T> void updateObject(String column, T type) {
        this.map.put(column, type);
    }

    public String getString(String column) {
        return get(column, String.class, null);
    }

    public Number getNumber(String column) {
        return get(column, Number.class, 0);
    }

    public boolean getBoolean(String column) {
        return get(column, boolean.class, false);
    }

    public World getWorld(String column) {
        return getWorld(column, "world");
    }

    public World getWorld(String column, String def) {
        return Bukkit.getWorld(get(column, String.class, def));
    }

    private <T> T get(String column, Class<T> type, T def) {
        Object obj = map.get(column);
        if (obj == null) return def;
        if (!type.isInstance(obj)) return def;
        return (T) obj;
    }
}