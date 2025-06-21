package com.project.PJA.user_act_log.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.user_act_log.dto.UserActionLog;
import com.project.PJA.user_act_log.enumeration.UserActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionLogService {

    private final ObjectMapper objectMapper;
    private final LogSenderService logSenderService;

    @Value("${log.path}")
    private String LOG_FILE_PATH;

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
            log.info("[USER ACTION] {}",jsonLog);

            Path path = Paths.get(LOG_FILE_PATH);
            Files.writeString(path, jsonLog + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            // 로그 전송 및 분석 저장
            logSenderService.sendLogsFromFile();
        } catch (Exception e) {
            log.error("사용자 로그 기록 중 오류 발생", e);
        }
    }
}
