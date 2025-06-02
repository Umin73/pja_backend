package com.project.PJA.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final StringRedisTemplate redisTemplate;

    public void saveEmailVerificationToken(String email, String token, long expirationMinutes) {
        redisTemplate.opsForValue().set("emailToken:" + email, token, Duration.ofMinutes(expirationMinutes));
    }

    public String getEmailVerificationToken(String email) {
        return redisTemplate.opsForValue().get("emailToken:" + email);
    }

    public void deleteEmailVerificationToken(String email) {
        redisTemplate.delete("emailToken:" + email);
    }

    public void saveFindPwVerificationToken(String email, String token, long expirationMinutes) {
        redisTemplate.opsForValue().set("findPwToken:" + email, token, Duration.ofMinutes(expirationMinutes));
    }

    public String getFindPwVerificationToken(String email) {
        return redisTemplate.opsForValue().get("findPwToken:" + email);
    }

    public void deleteFindPwVerificationToken(String email) {
        redisTemplate.delete("findPwToken:" + email);
    }
}
