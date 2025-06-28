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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
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

        // 담당자 불균형 분석(taskImbalanceResult) 저장
        saveTaskImbalanceResults(rootNode, workspaceId, now, mapper);

        // 평균 작업 처리 시간(AvgProcessingTimeResult) 저장 (기존 항목 삭제 후 덮어 씌움)
        saveProcessingTimeResults(rootNode, workspaceId, now, mapper);
    }

    private void saveTaskImbalanceResults(JsonNode rootNode, Long workspaceId, LocalDateTime now, ObjectMapper mapper) throws JsonProcessingException {
        JsonNode taskImbalanceNode = rootNode.get("task_imbalance").get("data");

        // 워크스페이스 아이디로 기존 값들 삭제
        taskImbalanceResultRepository.deleteByWorkspaceId(workspaceId);

        if (taskImbalanceNode != null && taskImbalanceNode.isArray()) {
            List<Map<String, Object>> imbalanceList = mapper.convertValue(taskImbalanceNode, new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> entry : imbalanceList) {
                TaskImbalanceResult result = new TaskImbalanceResult();
                result.setWorkspaceId(workspaceId);

                // participant_userId를 userId로 매핑
                Object userIdObj = entry.get("userId");
                Long userId = (userIdObj instanceof Number) ? ((Number) userIdObj).longValue() : Long.parseLong(userIdObj.toString());
                result.setUserId(userId);

                // details.state 처리
                Object stateObj = entry.get("details.state");
                String stateStr = stateObj.toString();
                try {
                    result.setState(Progress.valueOf(stateStr));
                } catch (IllegalArgumentException e) {
                    log.warn("State의 값이 잘못됨: " + stateStr);
                    continue;
                }

                // details.importance 처리
                Object importanceObj = entry.get("details.importance");
                Integer importance = (importanceObj instanceof Number) ? ((Number) importanceObj).intValue() : Integer.parseInt(importanceObj.toString());
                result.setImportance(importance);

                // count 처리
                Object countObj = entry.get("count");
                Integer taskCount = (countObj instanceof Number) ? ((Number) countObj).intValue() : Integer.parseInt(countObj.toString());
                result.setTaskCount(taskCount);

                result.setAnalyzedAt(now);

                taskImbalanceResultRepository.save(result);
            }
        }
    }

    private void saveProcessingTimeResults(JsonNode rootNode, Long workspaceId, LocalDateTime now, ObjectMapper mapper) throws JsonProcessingException {
        JsonNode processingTimeNode = rootNode.get("processing_time").get("data");

        // 워크스페이스 아이디로 기존 값들 삭제
        avgProcessingTimeResultRepository.deleteByWorkspaceId(workspaceId);

        if (processingTimeNode != null && processingTimeNode.isArray()) {
            List<Map<String, Object>> processingList = mapper.convertValue(processingTimeNode, new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> entry : processingList) {
                // participant_userId를 userId로 매핑
                Object userIdObj = entry.get("userId");
                Long userId = (userIdObj instanceof Number) ? ((Number) userIdObj).longValue() : Long.parseLong(userIdObj.toString());

                // details.importance 처리
                Object importanceObj = entry.get("details.importance");
                Integer importance = (importanceObj instanceof Number) ? ((Number) importanceObj).intValue() : Integer.parseInt(importanceObj.toString());

                // mean_hours가 null인 경우 처리
                Object meanHoursObj = entry.get("mean_hours");
                if (meanHoursObj == null || meanHoursObj.toString().equals("null") || meanHoursObj.toString().equalsIgnoreCase("NaN")) {
                    continue; // null이면 걍 건너뛱ㅁ
                }

                AvgProcessingTimeResult result = new AvgProcessingTimeResult();
                result.setWorkspaceId(workspaceId);
                result.setUserId(userId);
                result.setImportance(importance);

                long meanHours = 0L;
                if (meanHoursObj instanceof Number) {
                    meanHours = Math.round(((Number) meanHoursObj).doubleValue()); // 소수점 반올림
                } else {
                    meanHours = Math.round(Double.parseDouble(meanHoursObj.toString())); // 문자열이라면 double로 변환 후 반올림
                }
                result.setMeanHours(meanHours);

                result.setAnalyzedAt(now);

                avgProcessingTimeResultRepository.save(result);
            }
        }

    }

}
