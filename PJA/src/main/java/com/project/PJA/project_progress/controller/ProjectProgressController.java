package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.project_progress.dto.MyActionDto;
import com.project.PJA.project_progress.dto.ProjectProgressResponseDto;
import com.project.PJA.project_progress.dto.fullAiDto.AiRecommendationResponseDto;
import com.project.PJA.project_progress.service.ActionService;
import com.project.PJA.project_progress.service.ProjectProgressService;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/workspaces/")
@RequiredArgsConstructor
public class ProjectProgressController {

    private final ProjectProgressService projectProgressService;
    private final ActionService actionService;
    private final WorkspaceService workspaceService;

    @GetMapping("{workspaceId}/project/progress")
    ResponseEntity<SuccessResponse<?>> readProjectProgress(@AuthenticationPrincipal Users user,
                                                           @PathVariable("workspaceId") Long workspaceId) {
        log.info("== 프로젝트 진행 조회 API ==");
        ProjectProgressResponseDto dto = projectProgressService.getProjectProcessInfo(user.getUserId(), workspaceId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "프로젝트 진행 내용이 조회되었습니다.", dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("{workspaceId}/project/my-actions")
    ResponseEntity<SuccessResponse<?>> readMyActions(@AuthenticationPrincipal Users user,
                                                     @PathVariable("workspaceId")Long workspaceId) {
        log.info("== 내 작업(액션) 조회 API 진입 ==");

        List<MyActionDto> data = actionService.readMyToDoActionList(user, workspaceId);

        SuccessResponse<?>  response = new SuccessResponse<>("success", "내 작업(액션)을 성공적으로 조회하였습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("{workspaceId}/project/my-progress")
    ResponseEntity<SuccessResponse<?>> readMyProgress(@AuthenticationPrincipal Users user,
                                                      @PathVariable("workspaceId") Long workspaceId) {
        log.info("== 내 진행률 조회 API 진입 ==");
        Map<String, Object> data = actionService.readMyProgress(user, workspaceId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "내 진행률 조회에 성공하였습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("{workspaceId}/project/generation")
    ResponseEntity<SuccessResponse<?>> generateProjectProgress(@AuthenticationPrincipal Users user,
                                                            @PathVariable("workspaceId") Long workspaceId) {
        log.info("== 프로젝트 진행 카테고리,기능,액션 추천받기 API 진입 ==");

        AiRecommendationResponseDto data = projectProgressService.recommendFeatureStructure(user, workspaceId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "프로젝트 진행의 카테고리, 기능, 액션을 추천받았습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
