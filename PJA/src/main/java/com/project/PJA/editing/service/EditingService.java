package com.project.PJA.editing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.editing.dto.EditingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EditingService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void startEditing(Long userId, Long workspaceId, EditingRequest editingRequest) {
        String key = "editing:data:" + workspaceId + ":" + editingRequest.getPage();

    }

    public void startEditing(Long workspaceId, Long inputId, Long userId, String userName) {
        String key = "editing:data:" + workspaceId + ":" + inputId;
        Map<String, Object> data = Map.of(
                "userId", userId,
                "userName", userName,
                "timestamp", LocalDateTime.now().toString()
        );

        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, Duration.ofSeconds(5));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 실패", e);
        }
    }

    public void keepEditing(Long workspaceId, Long inputId) {
        String key = "editing:data:" + workspaceId + ":" + inputId;
        redisTemplate.expire(key, Duration.ofSeconds(5));
    }

    public void stopEditing(Long workspaceId, Long inputId) {
        String key = "editing:data:" + workspaceId + ":" + inputId;
        redisTemplate.delete(key);
    }

    public Optional<Map<String, Object>> getEditingStatus(Long workspaceId, Long inputId) {
        String key = "editing:data:" + workspaceId + ":" + inputId;
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) return Optional.empty();

        try {
            Map<String, Object> data = objectMapper.readValue(json, new TypeReference<>(){});
            return Optional.of(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 데이터 파싱 실패", e);
        }
    }

}
