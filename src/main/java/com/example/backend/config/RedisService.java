package com.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String email, String code) {
        redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);
    }

    public String get(String email) {
        System.out.println("이메일은!! " + email);
        Object result = redisTemplate.opsForValue().get(email);

        if(result == null) {
            throw new NullPointerException("다시 시도해 주세요");
        }
        return (String) result;
    }

    public void delete(String email) {
        redisTemplate.delete(email);
    }
}
