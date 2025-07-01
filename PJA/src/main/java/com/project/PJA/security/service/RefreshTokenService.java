package com.project.PJA.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String RT_PREFIX = "RT:";
    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 30; // 30일

    public void save(String uid, String token) {
        String key = RT_PREFIX + uid;
        redisTemplate.opsForValue().set(key, token, Duration.ofMillis(REFRESH_TOKEN_EXPIRATION));
    }

    public boolean isValid(String uid, String token) {
        String key = RT_PREFIX + uid;
        String storedToken = redisTemplate.opsForValue().get(key);
        return token.equals(storedToken);
    }

    public void delete(String uid) {
        String key = RT_PREFIX + uid;
        redisTemplate.delete(key);
    }
}
