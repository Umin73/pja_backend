package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.project_progress.dto.CreateActionDto;
import com.project.PJA.project_progress.dto.OnlyActionResponseDto;
import com.project.PJA.project_progress.dto.UpdateActionDto;
import com.project.PJA.project_progress.dto.aiDto.ActionRecommendationJson;
import com.project.PJA.project_progress.dto.aiDto.RecommendedAction;
import com.project.PJA.project_progress.service.ActionPostService;
import com.project.PJA.project_progress.service.ActionService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ActionController {

    private final ActionService actionService;
    private final ActionPostService actionPostService;

    @GetMapping("{workspaceId}/action")
    ResponseEntity<SuccessResponse<?>> getActionList(@AuthenticationPrincipal Users user,
                                                     @PathVariable("workspaceId") Long workspaceId) {

        log.info("== 액션 리스트 불러오기 API 진입 ==");

        List<OnlyActionResponseDto> data = actionService.readActionList(user, workspaceId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "액션 리스트 정보를 불러왔습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/action")
    ResponseEntity<SuccessResponse<?>> createAction(@AuthenticationPrincipal Users user,
                                                     @PathVariable("workspaceId") Long workspaceId,
                                                     @PathVariable("categoryId") Long categoryId,
                                                     @PathVariable("featureId") Long featureId,
                                                     @RequestBody CreateActionDto dto) {
        log.info("== 액션 생성 API 진입, {} ==", dto);
        Long actionId = actionService.createAction(user, workspaceId, categoryId, featureId, dto);
        Long actionPostId = actionPostService.getActionPostId(actionId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "액션이 생성되었습니다.",
                Map.of("actionId", actionId, "actionPostId", actionPostId));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{workspaceId}/feature/{featureId}/generation")
    public ResponseEntity<SuccessResponse<?>> generateAiAction(@AuthenticationPrincipal Users user,
                                                               @PathVariable("workspaceId") Long workspaceId,
                                                               @PathVariable("featureId") Long featureId) {
        log.info("== Action 추천받기 API 진입 ==");
        ActionRecommendationJson data = actionService.recommendedActions(user, workspaceId, featureId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "액션이 추천되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/action/{actionId}")
    public ResponseEntity<SuccessResponse<?>> updateAction(@AuthenticationPrincipal Users user,
                                                           @PathVariable("workspaceId") Long workspaceId,
                                                           @PathVariable("categoryId") Long categoryId,
                                                           @PathVariable("featureId") Long featureId,
                                                           @PathVariable("actionId") Long actionId,
                                                           @RequestBody UpdateActionDto dto) {
        Map<String, Object> data = actionService.updateAction(user, workspaceId, categoryId, featureId, actionId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "액션이 수정되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/action/{actionId}")
    public ResponseEntity<SuccessResponse<?>> deleteAction(@AuthenticationPrincipal Users user,
                                                           @PathVariable("workspaceId") Long workspaceId,
                                                           @PathVariable("categoryId") Long categoryId,
                                                           @PathVariable("featureId") Long featureId,
                                                           @PathVariable("actionId") Long actionId) {
        actionService.deleteAction(user, workspaceId, categoryId, featureId, actionId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "액션이 삭제되었습니다.", null);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
