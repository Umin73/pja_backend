package com.project.PJA.editing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.editing.dto.EditingRequest;
import com.project.PJA.editing.dto.EditingResponse;
import com.project.PJA.editing.dto.EditingUser;
import com.project.PJA.exception.ConflictException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EditingService {
    private final WorkspaceService workspaceService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EditingResponse startEditing(Long userId, String userName, Long workspaceId, EditingRequest editingRequest) {
        workspaceService.authorizeOwnerOrMemberOrThrowFromCache(userId, workspaceId);

        String key = "editing:data:" + workspaceId + ":" + editingRequest.getPage() + ":" + editingRequest.getField();

        EditingUser editingUser = new EditingUser(
                userId,
                userName,
                editingRequest.getField(),
                editingRequest.getFieldId()
        );

        try {
            String value = objectMapper.writeValueAsString(editingUser);

            Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(10));

            if (Boolean.FALSE.equals(success)) {
                throw new ConflictException("이미 다른 사용자가 편집 중입니다.");
            }

            return new EditingResponse(
                    userId,
                    userName,
                    editingRequest.getPage(),
                    editingRequest.getField(),
                    editingUser.getFieldId()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("편집 정보 직렬화 실패", e);
        }
    }

    public EditingResponse keepEditing(Long userId, String userName, Long workspaceId, EditingRequest editingRequest) {
        String key = "editing:data:" + workspaceId + ":" + editingRequest.getPage() + ":" + editingRequest.getField();
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            throw new NotFoundException("요청하신 편집 정보를 찾을 수 없습니다.");
        }

        try {
            EditingUser currentUser = objectMapper.readValue(value, EditingUser.class);

            if (!currentUser.getUserId().equals(userId)) {
                throw new ConflictException("이미 다른 사용자가 편집 중입니다.");
            }

            redisTemplate.expire(key, Duration.ofSeconds(10));

            return new EditingResponse(
                    userId,
                    userName,
                    editingRequest.getPage(),
                    editingRequest.getField(),
                    editingRequest.getFieldId()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("편집 정보 역직렬화 실패", e);
        }
    }

    public EditingResponse stopEditing(Long userId, String userName, Long workspaceId, EditingRequest editingRequest) {
        String key = "editing:data:" + workspaceId + ":" + editingRequest.getPage() + ":" + editingRequest.getField();
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            throw new NotFoundException("요청하신 편집 정보를 찾을 수 없습니다.");
        }

        try {
            EditingUser currentUser = objectMapper.readValue(value, EditingUser.class);

            if (!currentUser.getUserId().equals(userId)) {
                throw new ConflictException("이미 다른 사용자가 편집 중입니다.");
            }

            redisTemplate.delete(key);

            return new EditingResponse(
                    userId,
                    userName,
                    editingRequest.getPage(),
                    editingRequest.getField(),
                    editingRequest.getFieldId()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("편집 정보 역직렬화 실패", e);
        }
    }

    public List<EditingResponse> getEditingStatus(Long userId, Long workspaceId, String page) {
        // 권한 확인
        workspaceService.validateWorkspaceAccessFromCache(userId, workspaceId);
    }

    // 조회할 때 그냥 filed id받아서 하는걸로 그리고 생성하기 클릭하자마자 db에 저장되는 걸로

    /*public List<EditingResponse> getEditingStatus(Long userId, Long workspaceId, String page) {
        workspaceService.validateWorkspaceAccessFromCache(userId, workspaceId);
    }*/

    /*
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
    }*/

    /*
    public EditingUser getEditingStatus(Long workspaceId, EditingRequest req) {
        String key = getKey(workspaceId, req.getPage(), req.getField());
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) return null;

        try {
            return objectMapper.readValue(value, EditingUser.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }*/
}
