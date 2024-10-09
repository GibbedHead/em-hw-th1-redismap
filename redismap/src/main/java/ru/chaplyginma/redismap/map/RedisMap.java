package ru.chaplyginma.redismap.map;

import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import ru.chaplyginma.redismap.exception.RedisMapPutException;

import java.util.*;
import java.util.stream.Collectors;

public class RedisMap implements Map<String, String> {

    private final Jedis jedis;
    private final String prefix;
    private final String keysPattern;

    public RedisMap(Jedis jedis) {
        this.jedis = jedis;
        this.prefix = "redismap:" + UUID.randomUUID();
        this.keysPattern = prefix + ":*";
    }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            key = "null";
        }
        String realKey = addPrefix(key);

        String result = jedis.get(realKey);

        String setResult = jedis.set(realKey, value);
        if (!"OK".equals(setResult)) {
            throw new RedisMapPutException("Error putting key:`%s`, value:`%s`".formatted(key, value));
        }

        return result;
    }

    @Override
    public String remove(Object key) {
        if (key == null) {
            key = "null";
        }
        String realKey = addPrefix(key.toString());

        String result = jedis.get(realKey);

        jedis.del(realKey);

        return result;
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends String> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        jedis.keys(keysPattern).forEach(jedis::del);
    }

    @Override
    @NotNull
    public Set<String> keySet() {
        return jedis.keys(keysPattern).stream()
                .map(key -> key.substring((prefix + ":").length()))
                .collect(Collectors.toSet());
    }

    @Override
    @NotNull
    public Collection<String> values() {
        return jedis.keys(prefix + ":*").stream()
                .map(jedis::get)
                .toList();
    }


    @Override
    @NotNull
    public Set<Entry<String, String>> entrySet() {
        return jedis.keys(prefix + ":*").stream()
                .map(key -> {
                    String value = jedis.get(key);
                    String strippedKey = key.substring((prefix + ":").length());
                    return new AbstractMap.SimpleEntry<>(strippedKey, value);
                })
                .collect(Collectors.toSet());
    }

    @Override
    public String get(Object key) {
        if (key == null) {
            key = "null";
        }
        return jedis.get(addPrefix(String.valueOf(key)));
    }

    @Override
    public int size() {
        return jedis.keys(keysPattern).size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            key = "null";
        }
        return jedis.exists(addPrefix(key.toString()));
    }

    @Override
    public boolean containsValue(Object value) {
        return jedis.keys(prefix + ":*").stream()
                .map(jedis::get)
                .anyMatch(v -> v != null && v.equals(value));
    }

    private String addPrefix(String key) {
        return prefix + ":" + key;
    }
}
