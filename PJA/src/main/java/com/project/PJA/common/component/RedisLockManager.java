package com.project.PJA.common.component;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisLockManager {
    // Redis에 문자열 데이터를 주고받는 스프링 추상화 객체
    private final StringRedisTemplate redisTemplate;

    // key로 락 걸기
    public boolean acquireLock(String key, String value, long timeoutSeconds) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(timeoutSeconds));
        return Boolean.TRUE.equals(success);
    }

    // 락 해제
    public void releaseLock(String key, String expectedValue) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (expectedValue.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }

    // 락 갱신: 락이 걸려있고 락 주인이 맞으면 타임아웃 연장
    public boolean renewLock(String key, String expectedValue, long timeoutSeconds) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (expectedValue.equals(currentValue)) {
            // setExpire: key의 만료시간을 연장
            return redisTemplate.expire(key, Duration.ofSeconds(timeoutSeconds));
        }
        return false;
    }
}
