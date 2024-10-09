package ru.chaplyginma.redismap.map;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;

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

}