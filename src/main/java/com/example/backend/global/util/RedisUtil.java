package com.example.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }
}