package me.pheric.pcore.database;

import java.util.HashMap;

public class Result {

    private HashMap<String, Object> map = new HashMap<>();

    public void set(String column, Object object) {
        map.putIfAbsent(column, object);
    }

    public String getString(String column) {
        if (!map.containsKey(column)) return null;
        if (!(map.get(column) instanceof String)) return null;
        return (String) map.get(column);
    }

    public String getString(String column, String def) {
        if (!map.containsKey(column)) return def;
        if (!(map.get(column) instanceof String)) return def;
        return (String) map.getOrDefault(column, def);
    }

    public Number getNumber(String column) {
        if (!map.containsKey(column)) return 0L;
        if (!(map.get(column) instanceof Number)) return 0L;
        return (Number) map.getOrDefault(column, 0L);
    }

    public Number getNumber(String column, Number def) {
        if (!map.containsKey(column)) return def;
        if (!(map.get(column) instanceof Number)) return def;
        return (Number) map.getOrDefault(column, def);
    }

    public boolean getBoolean(String column) {
        if (!map.containsKey(column)) return false;
        if (!(map.get(column) instanceof Boolean)) return false;
        return (Boolean) map.getOrDefault(column, false);
    }

    public boolean getBoolean(String column, boolean def) {
        if (!map.containsKey(column)) return def;
        if (!(map.get(column) instanceof Boolean)) return def;
        return (Boolean) map.getOrDefault(column, false);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
