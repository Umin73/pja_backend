package com.project.PJA.actionAnalysis.controller;

import com.project.PJA.actionAnalysis.dto.AvgProcessingTimeGraphDto;
import com.project.PJA.actionAnalysis.dto.TaskImbalanceGraphDto;
import com.project.PJA.actionAnalysis.dto.TaskImbalanceResponseDto;
import com.project.PJA.actionAnalysis.service.ActionAnalysisQueryService;
import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/workspaces/")
@RequiredArgsConstructor
public class ActionAnalysisController {

    private final ActionAnalysisQueryService actionAnalysisQueryService;

    @GetMapping("{workspaceId}/task-imbalance")
    public ResponseEntity<SuccessResponse<?>> getTaskImbalance(@AuthenticationPrincipal Users user,
                                                               @PathVariable Long workspaceId) {
        TaskImbalanceResponseDto data = actionAnalysisQueryService.getTaskImbalanceGraph(user, workspaceId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "담당자 불균형 분석 그래프 조회에 성공하였습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("{workspaceId}/avg-processing-time")
    public ResponseEntity<SuccessResponse<?>> getAvgProcessingTime(@AuthenticationPrincipal Users user,
                                                                   @PathVariable Long workspaceId) {
        List<AvgProcessingTimeGraphDto> data = actionAnalysisQueryService.getAvgProcessingTimeGraph(user, workspaceId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "중요도에 따른 평균 작업 처리 시간 그래프 조회에 성공하였습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
