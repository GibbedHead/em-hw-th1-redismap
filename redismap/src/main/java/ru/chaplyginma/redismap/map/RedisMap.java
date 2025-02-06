package ru.chaplyginma.redismap.map;

import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.ScanResult;
import ru.chaplyginma.redismap.exception.RedisMapDeleteException;
import ru.chaplyginma.redismap.exception.RedisMapPutException;

import java.util.*;
import java.util.function.Consumer;

/**
 * A {@code RedisMap} is a Map implementation that stores key-value pairs in a Redis database.
 * This class utilizes the Jedis library to interact with Redis, providing a simple and efficient
 * way to manage data in a distributed environment.
 *
 * <p>
 * The keys are stored with a unique prefix to avoid collisions with other keys in the Redis database.
 * When using this class, all keys are treated as Strings, and values are also stored as Strings.
 * </p>
 *
 * <p>
 * This implementation follows the standard behavior of the {@link Map} interface, including methods
 * for adding, removing, and retrieving entries. It also provides methods to check for key existence,
 * clear the map, and retrieve key sets and value collections.
 * </p>
 *
 * <p>
 * Note that all operations that modify the map (such as {@code put}, {@code remove}, and {@code clear})
 * will directly affect the underlying Redis database.
 * </p>
 *
 * @see Jedis
 */
public class RedisMap implements Map<String, String> {

    private final Jedis jedis;
    private final String keysPattern;

    public RedisMap(Jedis jedis) {
        this.jedis = jedis;
        String prefix = "redismap:" + UUID.randomUUID();
        this.keysPattern = prefix + ":";
    }

    @Override
    public String put(String key, String value) throws RedisMapPutException {
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
    public String remove(Object key) throws RedisMapDeleteException {
        if (key == null) {
            key = "null";
        }
        String realKey = addPrefix(key.toString());

        String getOldResult = jedis.get(realKey);

        if (getOldResult == null) {
            return null;
        }

        if (jedis.del(realKey) > 0) {
            return getOldResult;
        }

        throw new RedisMapDeleteException("Error deleting key:`%s`".formatted(key));
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends String> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        jedis.flushAll();
    }

    @Override
    @NotNull
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        scanKeys(key -> {
            if (key.startsWith(keysPattern)) {
                keys.add(key.substring((keysPattern).length()));
            }
        });
        return keys;
    }

    @Override
    @NotNull
    public Collection<String> values() {
        List<String> values = new ArrayList<>();
        scanKeys(key -> {
            if (key.startsWith(keysPattern)) {
                values.add(jedis.get(key));
            }
        });
        return values;
    }


    @Override
    @NotNull
    public Set<Entry<String, String>> entrySet() {
        Set<Entry<String, String>> entries = new HashSet<>();
        scanKeys(key -> {
            if (key.startsWith(keysPattern)) {
                String value = jedis.get(key);
                String strippedKey = key.substring((keysPattern).length());
                entries.add(new AbstractMap.SimpleEntry<>(strippedKey, value));
            }
        });
        return entries;
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
        final int[] size = {0};
        scanKeys(key -> {
            if (key.startsWith(keysPattern)) {
                size[0]++;
            }
        });
        return size[0];
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
        final boolean[] found = {false};
        scanKeys(key -> {
            if (key.startsWith(keysPattern) && value.equals(jedis.get(key))) {
                found[0] = true;
            }
        });
        return found[0];
    }

    private String addPrefix(String key) {
        return keysPattern + key;
    }

    private void scanKeys(Consumer<String> keyConsumer) {
        String cursor = "0";
        do {
            ScanResult<String> scanResult = jedis.scan(cursor);
            List<String> foundKeys = scanResult.getResult();
            foundKeys.forEach(keyConsumer);
            cursor = scanResult.getCursor();
        } while (!cursor.equals("0"));
    }
}
