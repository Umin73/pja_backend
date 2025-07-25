package com.project.PJA.user_act_log.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.project_progress.entity.ActionParticipant;
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
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionLogService {

    private final ObjectMapper objectMapper;
    private final LogSenderService logSenderService;

    @Value("${log.dir}")
    private String LOG_FILE_DIR;

    public void log(UserActionType actionType,
                    String userId,
                    String username,
                    Long workspaceId,
                    Map<String, Object> details) {
        try {
            log.info("log_file_dir는 {}", LOG_FILE_DIR);

            UserActionLog actionLog = new UserActionLog();
            actionLog.setEvent(actionType);
            actionLog.setUserId(userId);
            actionLog.setUsername(username);
            actionLog.setTimestamp(ZonedDateTime.now(ZoneOffset.UTC));
            actionLog.setWorkspaceId(workspaceId);
            actionLog.setDetails(details);

            String jsonLog = objectMapper.writeValueAsString(actionLog);
            log.info("[USER ACTION] {}",jsonLog);

            // 경로 설정
            Path logDir = Paths.get(LOG_FILE_DIR);
            Files.createDirectories(logDir);

            Path logFilePath = logDir.resolve("user-actions-workspace-" + workspaceId + ".json");

            Files.writeString(
                    logFilePath,
                    jsonLog + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            // 로그 전송 및 분석 저장
            logSenderService.sendLogsFromFile(workspaceId);
        } catch (Exception e) {
            log.error("사용자 로그 기록 중 오류 발생", e);
        }
    }
}
