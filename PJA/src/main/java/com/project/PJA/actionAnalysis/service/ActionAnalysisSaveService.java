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
import com.project.PJA.project_progress.entity.ActionParticipant;
import com.project.PJA.project_progress.entity.Progress;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ActionAnalysisSaveService {

    private static final Logger log = LoggerFactory.getLogger(ActionAnalysisSaveService.class);
    private final WorkspaceService workspaceService;
    private final TaskImbalanceResultRepository taskImbalanceResultRepository;
    private final AvgProcessingTimeResultRepository avgProcessingTimeResultRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public void saveAnalysisResult(String responseBody, Long workspaceId, Set<Long> participants) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(responseBody);
        LocalDateTime now = LocalDateTime.now();

        // 담당자 불균형 분석(taskImbalanceResult) 저장 (이력 저장)
        String imbalanceRaw = rootNode.get("task_imbalance").path("data").asText();
        List<Map<String, Object>> imbalanceList = mapper
                .readValue(imbalanceRaw.replace("'", "\""),
                        new TypeReference<>() {});

        for(Map<String, Object> entry: imbalanceList) {

            Progress state = Progress.valueOf(entry.get("details.state").toString());
            Integer importance = (Integer) entry.get("details.importance");
            Integer count = (Integer) entry.get("count");

            for (Long userId : participants) {
                TaskImbalanceResult existing = taskImbalanceResultRepository
                        .findByWorkspaceIdAndUserIdAndStateAndImportance(workspaceId, userId, state, importance)
                        .orElse(null);

                if (existing != null) {
                    existing.setTaskCount(count);
                    existing.setAnalyzedAt(now);
                    taskImbalanceResultRepository.save(existing);
                } else {
                    TaskImbalanceResult result = new TaskImbalanceResult();
                    result.setWorkspaceId(workspaceId);
                    result.setUserId(userId);
                    result.setState(state);
                    result.setImportance(importance);
                    result.setTaskCount(count);
                    result.setAnalyzedAt(now);
                    taskImbalanceResultRepository.save(result);
                }
            }
        }

        // 평균 작업 처리 시간(AvgProcessingTimeResult) 저장 (기존 항목 삭제 후 덮어 씌움)
        String processingTimeRaw = rootNode.get("processing_time").path("data").asText();

        // JSON 파싱 가능한 형태로 정제
        String safeProcessingTimeRaw = processingTimeRaw.replace("'", "\"").replace("nan", "\"nan\"");

        List<Map<String, Object>> processingList = mapper.readValue(safeProcessingTimeRaw,  new TypeReference<>() {});

//        for(Map<String, Object> entry: processingList) {
//            Object userIdObj = entry.get("userId");
//            Long userId = (userIdObj instanceof Number) ? ((Number) userIdObj).longValue() : Long.parseLong(userIdObj.toString());
//
//            Object importanceObj = entry.get("details.importance");
//            Integer importance = (importanceObj instanceof Number) ? ((Number) importanceObj).intValue() : Integer.parseInt(importanceObj.toString());
//
//            // 기존 값 삭제(워크스페이스, 유저아이디, 중요도에 따라)
//            avgProcessingTimeResultRepository.deleteByWorkspaceIdAndUserIdAndImportance(workspaceId, userId, importance);
//
//            Object meanHoursObj = entry.get("mean_hours");
//            try {
//                String meanStr = meanHoursObj.toString();
//
//                if(meanStr.equalsIgnoreCase("nan")) {
//                    log.info("mean_hours 값이 NaN이므로 평균 작업 처리 시간 DB에는 저장X");
//                    continue;
//                }
//
//                long meanHours = 0L;
//
//                if (meanHoursObj instanceof Number) {
//                    meanHours = Math.round(((Number) meanHoursObj).doubleValue()); // 소수점 반올림
//                } else {
//                    meanHours = Math.round(Long.parseLong(meanHoursObj.toString())); // 문자열이라면 double로 변환 후 반올림
//                }
//
//                AvgProcessingTimeResult result = new AvgProcessingTimeResult();
//
//                result.setWorkspaceId(workspaceId);
//                result.setUserId(userId);
//                result.setImportance(importance);
//                result.setMeanHours(meanHours);
//                result.setAnalyzedAt(now);
//
//                avgProcessingTimeResultRepository.save(result);
//            } catch (Exception e) {
//                log.warn("mean_hours 파싱에 실패했습니다. (기본값 0 적용)");
//            }
//        }

        for (Map<String, Object> entry : processingList) {
            Object importanceObj = entry.get("details.importance");
            Integer importance = (importanceObj instanceof Number)
                    ? ((Number) importanceObj).intValue()
                    : Integer.parseInt(importanceObj.toString());

            Object meanHoursObj = entry.get("mean_hours");
            String meanStr = meanHoursObj.toString();

            if (meanStr.equalsIgnoreCase("nan")) {
                log.info("mean_hours 값이 NaN이므로 평균 작업 처리 시간 DB에는 저장X");
                continue;
            }

            long meanHours;
            try {
                if (meanHoursObj instanceof Number) {
                    meanHours = Math.round(((Number) meanHoursObj).doubleValue());
                } else {
                    meanHours = Math.round(Double.parseDouble(meanStr));
                }
            } catch (Exception e) {
                log.warn("mean_hours 파싱 실패 - 기본값 0 사용");
                meanHours = 0L;
            }


            for (Long userId : participants) {
                AvgProcessingTimeResult existing = avgProcessingTimeResultRepository
                        .findByWorkspaceIdAndUserIdAndImportance(workspaceId, userId, importance)
                        .orElse(null);

                if (existing != null) {
                    existing.setMeanHours(meanHours);
                    existing.setAnalyzedAt(now);
                    avgProcessingTimeResultRepository.save(existing);
                } else {
                    AvgProcessingTimeResult result = new AvgProcessingTimeResult();
                    result.setWorkspaceId(workspaceId);
                    result.setUserId(userId);
                    result.setImportance(importance);
                    result.setMeanHours(meanHours);
                    result.setAnalyzedAt(now);
                    avgProcessingTimeResultRepository.save(result);
                }
            }
        }

    }
}
