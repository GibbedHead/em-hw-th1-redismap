package ru.chaplyginma.redismap.map;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RedisMapTest {

    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:7.4.1-alpine");

    private static GenericContainer<?> redisContainer;
    private static Jedis jedis;
    private RedisMap redisMap;

    @BeforeAll
    static void setUpContainer() {
        redisContainer = new GenericContainer<>(REDIS_IMAGE)
                .withExposedPorts(6379);
        redisContainer.start();

        String host = redisContainer.getHost();
        Integer port = redisContainer.getFirstMappedPort();
        jedis = new Jedis(host, port);
    }

    @AfterAll
    static void tearDownContainer() {
        if (jedis != null) {
            jedis.close();
        }

        if (redisContainer != null) {
            redisContainer.stop();
        }
    }

    @BeforeEach
    void setUp() {
        redisMap = new RedisMap(jedis);
        jedis.flushDB();
    }

    @Test
    @DisplayName("Test normal put of non existed key")
    void givenValidNonExistedKeyValue_whenPut_thenReturnNull() {
        String putResult = redisMap.put("k1", "v1");

        assertThat(putResult).isNull();
    }

    @Test
    @DisplayName("Test normal put of existed key")
    void givenValidExistedKey_whenPut_thenReturnOldValue() {
        redisMap.put("key", "old");
        String putResult = redisMap.put("key", "new");

        assertThat(putResult).isEqualTo("old");
    }

    @Test
    @DisplayName("Test existing key remove")
    void givenValidExistingKey_whenRemove_thenReturnOldValue() {
        redisMap.put("key", "old");

        String removeResult = redisMap.remove("key");

        assertThat(removeResult).isEqualTo("old");
    }

    @Test
    @DisplayName("Test non existed key remove")
    void givenNonExistedKey_whenRemove_thenReturnNull() {
        String removeResult = redisMap.remove("key");

        assertThat(removeResult).isNull();
    }

    @Test
    @DisplayName("Test put all from map")
    void givenValidMap_whenPutAll_thenMapAdded() {
        redisMap.put("keyRedis", "valueRedis");

        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");

        redisMap.putAll(map);

        assertThat(redisMap).hasSize(4);
    }

    @Test
    @DisplayName("Test clear")
    void givenFilledRedisMap_whenClear_thenMapSizeIsZero() {
        redisMap.put("key1", "val1");
        redisMap.put("key2", "val2");
        redisMap.put("key3", "val3");

        redisMap.clear();

        assertThat(redisMap).isEmpty();
    }

    @Test
    @DisplayName("Test keySet from filled map")
    void givenFilledMap_whenKeySet_thenReturnKeySet() {
        redisMap.put("key1", "val1");
        redisMap.put("key2", "val2");
        redisMap.put("key3", "val3");

        Set<String> keySet = redisMap.keySet();

        assertThat(keySet).containsExactly("key1", "key2", "key3");
    }

    @Test
    @DisplayName("Test keySet from empty map")
    void givenEmptyMap_whenKeySet_thenReturnEmptySet() {
        Set<String> keySet = redisMap.keySet();

        assertThat(keySet).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Test vales() from filled map")
    void givenFilledMap_whenValues_thenReturnValues() {
        redisMap.put("key1", "val1");
        redisMap.put("key2", "val2");
        redisMap.put("key3", "val3");

        Collection<String> values = redisMap.values();

        assertThat(values).containsExactlyInAnyOrder("val1", "val2", "val3");
    }

    @Test
    @DisplayName("Test values() from empty map")
    void givenEmptyMap_whenValues_thenReturnEmptyCollection() {
        Collection<String> values = redisMap.values();

        assertThat(values).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Test entrySet() from filled map")
    void givenFilledMap_whenEntrySet_thenReturnEntrySet() {
        redisMap.put("key1", "val1");
        redisMap.put("key2", "val2");
        redisMap.put("key3", "val3");

        Set<Map.Entry<String, String>> entrySet = redisMap.entrySet();

        assertThat(entrySet).hasSize(3);
    }

    @Test
    @DisplayName("Test entrySet() from empty map")
    void givenEmptyMap_whenEntrySet_thenReturnEmptySet() {
        Set<Map.Entry<String, String>> entrySet = redisMap.entrySet();

        assertThat(entrySet).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Test get existing key")
    void givenExistingKey_whenGet_thenReturnValue() {
        redisMap.put("key1", "val1");

        assertThat(redisMap.get("key1")).isEqualTo("val1");

    }

    @Test
    @DisplayName("Test get non existing key")
    void givenNonExistingKey_whenGet_thenReturnNull() {
        assertThat(redisMap.get("key1")).isNull();
    }

    @Test
    @DisplayName("Test size on filled map")
    void givenFilledMap_whenSize_thenReturnSize() {
        redisMap.put("key1", "val1");
        redisMap.put("key2", "val2");
        redisMap.put("key3", "val3");

        int size = redisMap.size();

        assertThat(size).isEqualTo(3);
    }

    @Test
    @DisplayName("Test size on empty map")
    void givenEmptyMap_whenSize_thenReturnZero() {
        int size = redisMap.size();

        assertThat(size).isZero();
    }

    @Test
    @DisplayName("Test isEmpty on filled map")
    void givenFilledMap_whenIsEmpty_thenReturnFalse() {
        redisMap.put("key1", "val1");
        redisMap.put("key2", "val2");
        redisMap.put("key3", "val3");

        boolean isEmpty = redisMap.isEmpty();

        assertThat(isEmpty).isFalse();
    }

    @Test
    @DisplayName("Test isEmpty on empty map")
    void givenEmptyMap_whenIsEmpty_thenReturnTrue() {
        boolean isEmpty = redisMap.isEmpty();

        assertThat(isEmpty).isTrue();
    }

    @Test
    @DisplayName("Test containKey when key exist")
    void givenExistedKey_whenContainsKey_thenReturnTrue() {
        redisMap.put("key1", "val1");
        redisMap.put("key2", "val2");
        redisMap.put("key3", "val3");

        boolean containKey = redisMap.containsKey("key1");

        assertThat(containKey).isTrue();
    }

    @Test
    @DisplayName("Test containKey when not key exist")
    void givenNotExistedKey_whenContainsKey_thenReturnFalse() {
        redisMap.put("key1", "val1");

        assertThat(redisMap.containsKey("key2")).isFalse();
    }

    @Test
    @DisplayName("Test containValue when value exist")
    void givenExistedValue_whenContainsValue_thenReturnTrue() {
        redisMap.put("key1", "val1");
        redisMap.put("key2", "val2");
        redisMap.put("key3", "val3");

        boolean containValue = redisMap.containsValue("val1");

        assertThat(containValue).isTrue();
    }

    @Test
    @DisplayName("Test containValue when value not exist")
    void givenNotExistedValue_whenContainsValue_thenReturnFalse() {
        assertThat(redisMap.containsValue("val1")).isFalse();
    }
}