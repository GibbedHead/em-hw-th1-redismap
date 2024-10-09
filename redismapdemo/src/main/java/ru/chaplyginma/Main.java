package ru.chaplyginma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import ru.chaplyginma.redismap.map.RedisMap;

import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        RedisMap redisMap = new RedisMap(jedis);

        logger.info("Adding 3 entries to redis map");
        redisMap.put("key1", "value1");
        redisMap.put("key2", "value2");
        redisMap.put("key3", "value3");

        logger.info("for 'key1' value is '{}'", redisMap.get("key1"));

        logger.info("Adding map to redis map");
        Map<String, String> toAdd = new HashMap<>();
        toAdd.put("toAdd_key1", "toAdd_value1");
        toAdd.put("toAdd_key2", "toAdd_value2");
        redisMap.putAll(toAdd);
        logger.info("Getting val for added map key 'toAdd_key1': {}", redisMap.get("toAdd_key1"));

        logger.info("New map size: {}", redisMap.size());

        logger.info("Removing key 'toAdd_key2' from redis map");
        redisMap.remove("toAdd_key2");
        logger.info("After removing key 'toAdd_key2' from redis map new size: {}", redisMap.size());

        logger.info("Is map empty? '{}'", redisMap.isEmpty());

        logger.info("Keys of a map are: {}", redisMap.keySet());
        logger.info("Values of a map are: {}", redisMap.values());

        logger.info("Does map contains key 'toAdd_key1'? '{}'", redisMap.containsKey("toAdd_key1"));
        logger.info("Does map contains value 'toAdd_value1'? '{}'", redisMap.containsValue("toAdd_value1"));
    }
}