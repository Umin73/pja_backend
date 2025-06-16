package com.project.PJA.common.user_act_log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionLogService {

    private final ObjectMapper objectMapper;

    public void log(UserActionType actionType,
                    String userId,
                    String username,
                    Long workspaceId,
                    Map<String, Object> details) {
        try {
            UserActionLog actionLog = new UserActionLog();
            actionLog.setEvent(actionType);
            actionLog.setUserId(userId);
            actionLog.setUsername(username);
            actionLog.setTimestamp(ZonedDateTime.now(ZoneOffset.UTC));
            actionLog.setWorkspaceId(workspaceId);
            actionLog.setDetails(details);

            String jsonLog = objectMapper.writeValueAsString(actionLog);
            log.info(jsonLog);

        } catch (Exception e) {
            log.error("사용자 로그 기록 중 오류 발생", e);
        }
    }
}
