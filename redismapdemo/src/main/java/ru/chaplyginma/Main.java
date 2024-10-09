package ru.chaplyginma;

import redis.clients.jedis.Jedis;
import ru.chaplyginma.redismap.map.RedisMap;

public class Main {
    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        RedisMap map = new RedisMap(jedis);

        map.put("k1", "v1");
        map.put("k2", "v2");
        map.put("k3", "v3");

        System.out.println(map.get("k1"));

        map.remove("k1");

        System.out.println(map.get("k1"));

        System.out.println(map.keySet());
        System.out.println(map.values());
        System.out.println(map.entrySet());

        System.out.println(map.containsKey("k1"));
        System.out.println(map.containsKey("k2"));


    }
}