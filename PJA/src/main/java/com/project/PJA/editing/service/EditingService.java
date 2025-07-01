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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EditingService {
    private final WorkspaceService workspaceService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    // 편집 시작
    public EditingResponse startEditing(Long userId, String userName, String userProfile, Long workspaceId, EditingRequest request) {
        workspaceService.authorizeOwnerOrMemberOrThrowFromCache(userId, workspaceId);

        String editingUserKey = makeEditingUserRedisKey(userId);
        String editingKey = makeEditingRedisKey(workspaceId, request.getPage(), request.getField(), request.getFieldId());

        EditingUser editingUser = new EditingUser(
                userId,
                userName,
                userProfile,
                request.getField(),
                request.getFieldId()
        );

        String luaScript = """
                local existingFieldKey = redis.call('get', KEYS[1])
                if existingFieldKey then
                    redis.call('del', existingFieldKey)
                end
                redis.call('del', KEYS[1])
                
                if redis.call('exists', KEYS[2]) == 1 then
                    return false
                end
                
                redis.call('set', KEYS[1], ARGV[1], 'EX', 15)
                redis.call('set', KEYS[2], ARGV[2], 'EX', 15)
                return true
                """;

        String jsonEditingUser;
        try {
            jsonEditingUser = objectMapper.writeValueAsString(editingUser);
        } catch (JsonProcessingException e) {
            log.error("편집 시작 직렬화 실패", e);
            throw new RuntimeException("편집 정보 직렬화 실패", e);
        }

        Boolean success = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            byte[] lua = redisTemplate.getStringSerializer().serialize(luaScript);

            byte[] key1 = redisTemplate.getStringSerializer().serialize(editingUserKey);
            byte[] key2 = redisTemplate.getStringSerializer().serialize(editingKey);

            byte[] arg1 = redisTemplate.getStringSerializer().serialize(editingKey);
            byte[] arg2 = redisTemplate.getStringSerializer().serialize(jsonEditingUser);

            Object evalResult = connection.eval(lua, ReturnType.BOOLEAN, 2, key1, key2, arg1, arg2);
            return (Boolean) evalResult;
        });

        if (Boolean.FALSE.equals(success)) {
            throw new ConflictException("이미 다른 사용자가 편집 중입니다.");
        }

        return new EditingResponse(
                userId,
                userName,
                userProfile,
                request.getPage(),
                request.getField(),
                request.getFieldId()
        );
    }

    // 편집 유지
    public EditingResponse keepEditing(Long userId, String userName, String userProfile, Long workspaceId, EditingRequest request) {
        String editingUserKey = makeEditingUserRedisKey(userId);
        String editingKey = makeEditingRedisKey(workspaceId, request.getPage(), request.getField(), request.getFieldId());

        String luaScript = """
                local existingFieldKey = redis.call('get', KEYS[1])
                if not existingFieldKey or existingFieldKey ~= KEYS[2] then
                    return "NOT_FOUND"
                end
                
                local editingData = redis.call('get', KEYS[2])
                if not editingData then
                    return "NOT_FOUND"
                end
                
                local success, editingUser = pcall(cjson.decode, editingData)
                if not success then
                    return "INVALID_JSON"
                end
                if editingUser.userId ~= tonumber(ARGV[1]) then
                    return "CONFLICT"
                end
                
                redis.call('expire', KEYS[1], 15)
                redis.call('expire', KEYS[2], 15)
                
                return "OK"
                """;

        String result = redisTemplate.execute((RedisCallback<String>) connection -> {
            byte[] lua = redisTemplate.getStringSerializer().serialize(luaScript);

            byte[] key1 = redisTemplate.getStringSerializer().serialize(editingUserKey);
            byte[] key2 = redisTemplate.getStringSerializer().serialize(editingKey);

            byte[] arg1 = redisTemplate.getStringSerializer().serialize(String.valueOf(userId));

            Object evalResult = connection.eval(lua, ReturnType.VALUE, 2, key1, key2, arg1);
            if (evalResult instanceof byte[]) {
                return redisTemplate.getStringSerializer().deserialize((byte[]) evalResult);
            } else if (evalResult instanceof String) {
                return (String) evalResult;
            } else {
                return null;
            }
        });

        switch (result) {
            case "OK":
                break;
            case "NOT_FOUND":
                throw new NotFoundException("요청하신 편집 정보를 찾을 수 없습니다.");
            case "CONFLICT":
                throw new ConflictException("이미 다른 사용자가 편집 중입니다.");
            case "INVALID_JSON":
                throw new RuntimeException("편집 중인 데이터가 손상되어 처리할 수 없습니다.");
            default:
                throw new RuntimeException("알 수 없는 Redis 응답: " + result);
        }

        return new EditingResponse(
                userId,
                userName,
                userProfile,
                request.getPage(),
                request.getField(),
                request.getFieldId()
        );
    }

    // 편집 삭제
    public EditingResponse stopEditing(Long userId, String userName, String userProfile, Long workspaceId, EditingRequest request) {
        String editingUserKey = makeEditingUserRedisKey(userId);
        String editingKey = makeEditingRedisKey(workspaceId, request.getPage(), request.getField(), request.getFieldId());

        String luaScript = """
                local existingFieldKey = redis.call('get', KEYS[1])
                if not existingFieldKey or existingFieldKey ~= KEYS[2] then
                    return "NOT_FOUND"
                end
                
                local editingData = redis.call('get', KEYS[2])
                if not editingData then
                    return "NOT_FOUND"
                end
                
                local success, editingUser = pcall(cjson.decode, editingData)
                if not success then
                    return "INVALID_JSON"
                end
                if editingUser.userId ~= tonumber(ARGV[1]) then
                    return "CONFLICT"
                end
                
                redis.call('del', KEYS[1])
                redis.call('del', KEYS[2])
                
                return "OK"
                """;

        String result = redisTemplate.execute((RedisCallback<String>) connection -> {
            byte[] lua = redisTemplate.getStringSerializer().serialize(luaScript);

            byte[] key1 = redisTemplate.getStringSerializer().serialize(editingUserKey);
            byte[] key2 = redisTemplate.getStringSerializer().serialize(editingKey);

            byte[] arg1 = redisTemplate.getStringSerializer().serialize(String.valueOf(userId));

            Object evalResult = connection.eval(lua, ReturnType.VALUE, 2, key1, key2, arg1);
            if (evalResult instanceof byte[]) {
                return redisTemplate.getStringSerializer().deserialize((byte[]) evalResult);
            } else if (evalResult instanceof String) {
                return (String) evalResult;
            } else {
                return null;
            }
        });

        switch (result) {
            case "OK":
                break;
            case "NOT_FOUND":
                //throw new NotFoundException("요청하신 편집 정보를 찾을 수 없습니다.");
                break;
            case "CONFLICT":
                throw new ConflictException("이미 다른 사용자가 편집 중입니다.");
            case "INVALID_JSON":
                throw new RuntimeException("편집 중인 데이터가 손상되어 처리할 수 없습니다.");
            default:
                throw new RuntimeException("알 수 없는 Redis 응답: " + result);
        }

        return new EditingResponse(
                userId,
                userName,
                userProfile,
                request.getPage(),
                request.getField(),
                request.getFieldId()
        );
    }

    // 편집 상태 조회
    public List<EditingResponse> getEditingStatus(Long userId, Long workspaceId, String page) {
        // 권한 확인
        workspaceService.validateWorkspaceAccessFromCache(userId, workspaceId);

        // 확인했으니 보여주기
        String pattern = "editing:data:" + workspaceId + ":" + page + ":*";
        List<EditingResponse> responses = new ArrayList<>();

        try(Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .scan(ScanOptions.scanOptions().match(pattern).count(1000).build())) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                String value = redisTemplate.opsForValue().get(key);
                if (value == null) continue;

                try {
                    EditingUser editingUser = objectMapper.readValue(value, EditingUser.class);

                    if (editingUser.getUserId().equals(userId)) {
                        continue;
                    }

                    String[] parts = key.split(":");
                    String parsedField = null;
                    String parsedFieldId = null;

                    if ("requirements".equals(page) || "project-info".equals(page) || "apis".equals(page) || "action".equals(page)) {
                        // editing:data:{workspaceId}:{page}:{fieldId}
                        if (parts.length == 5) {
                            parsedFieldId = parts[4];
                        }
                    } else {
                        // editing:data:{workspaceId}:{page}:{field}
                        if (parts.length == 5) {
                            parsedField = parts[4];
                        }
                        // editing:data:{workspaceId}:{page}:{field}:{fieldId}
                        else if (parts.length == 6) {
                            parsedField = parts[4];
                            parsedFieldId = parts[5];
                        }
                    }

                    EditingResponse response = new EditingResponse(
                            editingUser.getUserId(),
                            editingUser.getUserName(),
                            editingUser.getUserProfile(),
                            page,
                            parsedField,
                            parsedFieldId
                    );

                    responses.add(response);
                } catch (JsonProcessingException e) {
                    log.error("편집 정보 역직렬화 실패", e);
                    continue;
                }
            }
        }

        return responses;
    }

    private String makeEditingUserRedisKey(Long userId) {
        return "editing:user:" + userId;
    }

    private String makeEditingRedisKey(Long workspaceId, String page, String field, String fieldId) {
        if (page.equals("requirements") || page.equals("project-info") || page.equals("apis") || page.equals("action")) {
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
}
