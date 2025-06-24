package com.project.PJA.editing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.editing.dto.*;
import com.project.PJA.exception.ConflictException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EditingService {
    private final WorkspaceService workspaceService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 편집 시작
    public EditingResponse startEditing(Long userId, String userName, Long workspaceId, EditingRequest request) {
        workspaceService.authorizeOwnerOrMemberOrThrowFromCache(userId, workspaceId);

        String key = makeRedisKey(workspaceId, request.getPage(), request.getField(), request.getFieldId());

        EditingUser editingUser = new EditingUser(
                userId,
                userName,
                request.getField(),
                request.getFieldId(),
                request.getContent()
        );

        try {
            String value = objectMapper.writeValueAsString(editingUser);

            Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(15));

            if (Boolean.FALSE.equals(success)) {
                throw new ConflictException("이미 다른 사용자가 편집 중입니다.");
            }

            return new EditingResponse(
                    userId,
                    userName,
                    request.getPage(),
                    request.getField(),
                    request.getFieldId(),
                    request.getContent()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("편집 정보 직렬화 실패", e);
        }
    }

    // 편집 유지
    public EditingResponse keepEditing(Long userId, String userName, Long workspaceId, EditingRequest request) {
        String key = makeRedisKey(workspaceId, request.getPage(), request.getField(), request.getFieldId());
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            throw new NotFoundException("요청하신 편집 정보를 찾을 수 없습니다.");
        }

        try {
            EditingUser currentUser = objectMapper.readValue(value, EditingUser.class);

            if (!currentUser.getUserId().equals(userId)) {
                throw new ConflictException("이미 다른 사용자가 편집 중입니다.");
            }

            redisTemplate.expire(key, Duration.ofSeconds(15));

            return new EditingResponse(
                    userId,
                    userName,
                    request.getPage(),
                    request.getField(),
                    request.getFieldId(),
                    request.getContent()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("편집 정보 역직렬화 실패", e);
        }
    }

    // 편집 삭제
    public EditingResponse stopEditing(Long userId, String userName, Long workspaceId, EditingRequest request) {
        String key = makeRedisKey(workspaceId, request.getPage(), request.getField(), request.getFieldId());
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
                    request.getPage(),
                    request.getField(),
                    request.getFieldId(),
                    request.getContent()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("편집 정보 역직렬화 실패", e);
        }
    }

    // 편집 상태 조회
    public List<EditingResponse> getEditingStatus(Long userId, Long workspaceId, String page) {
        // 권한 확인
        workspaceService.validateWorkspaceAccessFromCache(userId, workspaceId);

        // 확인했으니 보여주기
        String pattern = "editing:data:" + workspaceId + ":" + page + ":*";
        List<EditingResponse> responses = new ArrayList<>();
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .scan(ScanOptions.scanOptions().match(pattern).count(1000).build());

        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) continue;

            try {
                EditingUser editingUser = objectMapper.readValue(value, EditingUser.class);

                String[] parts = key.split(":");
                String parsedField = null;
                Long parsedFieldId = null;

                if (page.equals("project-info") || page.equals("apis") || page.equals("action")) {
                    // editing:data:{workspaceId}:{page}:{fieldId}
                    if (parts.length == 5) {
                        parsedFieldId = Long.valueOf(parts[4]);
                    }
                } else {
                    // editing:data:{workspaceId}:{page}:{field}
                    if (parts.length == 5) {
                        parsedField = parts[4];
                    }
                    // editing:data:{workspaceId}:{page}:{field}:{fieldId}
                    else if (parts.length == 6) {
                        parsedField = parts[4];
                        parsedFieldId = Long.valueOf(parts[5]);
                    }
                }

                EditingResponse response = new EditingResponse(
                        editingUser.getUserId(),
                        editingUser.getUserName(),
                        page,
                        parsedField,
                        parsedFieldId,
                        editingUser.getContent()
                );

                responses.add(response);
            } catch (JsonProcessingException e) {
                log.error("편집 정보 역직렬화 실패", e);
                continue;
            }
        }
        return responses;
    }

    private String makeRedisKey(Long workspaceId, String page, String field, Long fieldId) {
        if (page.equals("project-info") || page.equals("apis") || page.equals("action")) {
            // 키 editing:data:{workspaceId}:{page}:{fieldId} 형태 (row 단위)
            return "editing:data:" + workspaceId + ":" + page + ":" + fieldId;
        }
        if (fieldId != null) {
            // 키 editing:data:{workspaceId}:{page}:{field}:{fieldId} 형태
            return "editing:data:" + workspaceId + ":" + page + ":" + field + ":" + fieldId;
        }
        // 키 editing:data:{workspaceId}:{page}:{field} 형태
        return "editing:data:" + workspaceId + ":" + page + ":" + field;
    }

    // 아이디어 입력 편집 시작
    public void startIdeaInputEditing(Long userId, String userName, String userProfile, Long workspaceId, IdeaInputEditingRequest request) {
        workspaceService.authorizeOwnerOrMemberOrThrowFromCache(userId, workspaceId);

        String key = "editing:data:" + workspaceId + ":idea-input";

        IdeaInputEditingUser editingUser = new IdeaInputEditingUser(
                userId,
                userName,
                userProfile,
                request.getField(),
                request.getFieldId(),
                request.getContent()
        );

        String jsonArrayStr = redisTemplate.opsForValue().get(key);
        List<IdeaInputEditingUser> editingUsers;

        if (jsonArrayStr == null || jsonArrayStr.isEmpty()) {
            editingUsers = new ArrayList<>();
        } else {
            try {
                editingUsers = objectMapper.readValue(jsonArrayStr, new TypeReference<List<IdeaInputEditingUser>>() {});
            } catch (JsonProcessingException e) {
                log.error("JSON 파싱 에러:", e);
                editingUsers = new ArrayList<>();
            }
        }

        editingUsers.add(editingUser);

        try {
            String updatedJsonArrayStr = objectMapper.writeValueAsString(editingUsers);
            redisTemplate.opsForValue().set(key, updatedJsonArrayStr);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 에러:", e);
        }
    }
}
