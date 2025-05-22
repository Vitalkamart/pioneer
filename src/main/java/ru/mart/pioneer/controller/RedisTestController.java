package ru.mart.pioneer.controller;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisTestController {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisTestController(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @GetMapping("/redis-test")
    public String testRedis() {
        try {
            redisConnectionFactory.getConnection().ping();
            return "Redis connection OK!";
        } catch (Exception e) {
            return "Redis connection FAILED: " + e.getMessage();
        }
    }
}
