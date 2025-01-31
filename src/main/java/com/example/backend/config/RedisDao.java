package com.example.backend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class RedisDao {
    private final RedisTemplate<String, String> redisTemplate;
    public RedisDao(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setValues(String key, String data) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValuesList(String key, String data) {
        redisTemplate.opsForList().rightPushAll(key,data);
    }

    public List<String> getValuesList(String key) {
        Long len = redisTemplate.opsForList().size(key);
        return len == 0 ? new ArrayList<>() : redisTemplate.opsForList().range(key, 0, len-1);
    }

    public String getValues(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public void setKeyExpiry(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }

    public Set<String> getAllKeys(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        return keys;
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }
}