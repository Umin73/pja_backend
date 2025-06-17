package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.common.user_act_log.UserActionLogService;
import com.project.PJA.common.user_act_log.UserActionType;
import com.project.PJA.project_progress.dto.CreateProgressDto;
import com.project.PJA.project_progress.dto.UpdateProgressDto;
import com.project.PJA.project_progress.service.ActionService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/workspaces/")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    @PostMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/action")
    ResponseEntity<SuccessResponse<?>> createAction(@AuthenticationPrincipal Users user,
                                                     @PathVariable("workspaceId") Long workspaceId,
                                                     @PathVariable("categoryId") Long categoryId,
                                                     @PathVariable("featureId") Long featureId,
                                                     @RequestBody CreateProgressDto dto) {
        log.info("== 액션 생성 API 진입, {} ==", dto);
        Long data = actionService.createAction(user, workspaceId, categoryId, featureId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "액션이 생성되었습니다.", data);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/action/{actionId}")
    public ResponseEntity<SuccessResponse<?>> updateAction(@AuthenticationPrincipal Users user,
                                                           @PathVariable("workspaceId") Long workspaceId,
                                                           @PathVariable("categoryId") Long categoryId,
                                                           @PathVariable("featureId") Long featureId,
                                                           @PathVariable("actionId") Long actionId,
                                                           @RequestBody UpdateProgressDto dto) {
        actionService.updateAction(user, workspaceId, categoryId, featureId, actionId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "액션이 수정되었습니다.", null);

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
