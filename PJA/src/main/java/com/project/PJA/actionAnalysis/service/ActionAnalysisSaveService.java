package com.project.PJA.actionAnalysis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.actionAnalysis.entity.AvgProcessingTimeResult;
import com.project.PJA.actionAnalysis.entity.TaskImbalanceResult;
import com.project.PJA.actionAnalysis.repository.AvgProcessingTimeResultRepository;
import com.project.PJA.actionAnalysis.repository.TaskImbalanceResultRepository;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.project_progress.entity.Progress;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActionAnalysisSaveService {

    private final WorkspaceService workspaceService;
    private final TaskImbalanceResultRepository taskImbalanceResultRepository;
    private final AvgProcessingTimeResultRepository avgProcessingTimeResultRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public void saveAnalysisResult(String responseBody, Long workspaceId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(responseBody);
        LocalDateTime now = LocalDateTime.now();

        // 담당자 불균형 분석(taskImbalanceResult) 저장 (이력 저장)
        String imbalanceRaw = rootNode.get("task_imbalance").path("data").asText();
        List<Map<String, Object>> imbalanceList = mapper
                .readValue(imbalanceRaw.replace("'", "\""),
                        new TypeReference<>() {});

        for(Map<String, Object> entry: imbalanceList) {
            TaskImbalanceResult result = new TaskImbalanceResult();
            result.setWorkspaceId(workspaceId);

            Object userIdObj = entry.get("userId");
            Long userId = (userIdObj instanceof Number) ? ((Number) userIdObj).longValue() : Long.parseLong(userIdObj.toString());
            result.setUserId(userId);

            Object stateObj = entry.get("details.state");
            String stateStr = stateObj.toString();
            result.setState(Progress.valueOf(stateStr));

            result.setImportance((Integer) entry.get("details.importance"));
            result.setTaskCount((Integer) entry.get("count"));
            result.setAnalyzedAt(now);

            taskImbalanceResultRepository.save(result);
        }

        // 평균 작업 처리 시간(AvgProcessingTimeResult) 저장 (기존 항목 삭제 후 덮어 씌움)
        String processingTimeRaw = rootNode.get("processing_time").path("data").asText();
        List<Map<String, Object>> processingList = mapper.readValue(processingTimeRaw.replace("'", "\""), new TypeReference<>() {});

        for(Map<String, Object> entry: processingList) {
            Object userIdObj = entry.get("userId");
            Long userId = (userIdObj instanceof Number) ? ((Number) userIdObj).longValue() : Long.parseLong(userIdObj.toString());

            Object importanceObj = entry.get("details.importance");
            Integer importance = (importanceObj instanceof Number) ? ((Number) importanceObj).intValue() : Integer.parseInt(importanceObj.toString());

            // 기존 값 삭제(워크스페이스, 유저아이디, 중요도에 따라)
            avgProcessingTimeResultRepository.deleteByWorkspaceIdAndUserIdAndImportance(workspaceId, userId, importance);

            AvgProcessingTimeResult result = new AvgProcessingTimeResult();
            result.setWorkspaceId(workspaceId);
            result.setUserId(userId);
            result.setImportance(importance);

            Object meanHoursObj = entry.get("mean_hours");
            long meanHours = 0L;
            if (meanHoursObj instanceof Number) {
                meanHours = Math.round(((Number) meanHoursObj).doubleValue()); // 소수점 반올림
            } else {
                meanHours = Math.round(Long.parseLong(meanHoursObj.toString())); // 문자열이라면 double로 변환 후 반올림
            }
            result.setMeanHours(meanHours);

            result.setAnalyzedAt(now);

            avgProcessingTimeResultRepository.save(result);
        }
    }
}
