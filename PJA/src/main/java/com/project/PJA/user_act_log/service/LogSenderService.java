package com.project.PJA.user_act_log.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.actionAnalysis.service.ActionAnalysisSaveService;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.user_act_log.dto.UserActionLogParsing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogSenderService {

    private final UserRepository userRepository;
    @Value("${ml.api.url}")
    private String mlApiUrl;

//    @Value("${log.path}")
    @Value("${log.dir}")
    private String logFileDir;

    private final RestTemplate restTemplate;
    private final ActionAnalysisSaveService analysisSaveService;

    public void sendLogsFromFile(Long workspaceId) {
        try {
            String fileName = "user-actions-workspace-" + workspaceId + ".json";
            Path path = Paths.get(logFileDir, fileName);
            log.info("유저 액션 로그 경로: {}", path.toAbsolutePath());

            List<String> lines = Files.readAllLines(path);
            ObjectMapper objectMapper = new ObjectMapper();

            List<UserActionLogParsing> logs = lines.stream()
                    .map(line -> {
                        try {
                            return objectMapper.readValue(line, UserActionLogParsing.class);
                        } catch (IOException e) {
                            log.warn("JSON 파싱 실패: {}", line);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

            if (logs.isEmpty()) {
                log.info("전송할 유저 액션 로그가 없습니다.");
                return;
            }

            // logs 리스트를 JSON 문자열로 변환 (JSON array)
            String logJsonArrayString = objectMapper.writeValueAsString(logs);

            Map<String, String> body = Map.of("user_log", logJsonArrayString);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(mlApiUrl, request, String.class);
            log.info("User Action 로그 전송 완료: {}", response.getStatusCode());
            log.info("응답 내용: {}", response.getBody());

            // 분석 결과 DB에 저장
//            UserActionLogParsing firstLog = logs.get(0);
//            Long workspaceId = firstLog.getWorkspaceId();

            analysisSaveService.saveAnalysisResult(response.getBody(), workspaceId);

        } catch (IOException e) {
            log.error("User Action 로그 파일 읽기 실패", e);
        } catch (Exception e) {
            log.error("User Action 로그 전송 실패", e);
        }
    }
}
