package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.common.user_act_log.UserActionLogService;
import com.project.PJA.common.user_act_log.UserActionType;
import com.project.PJA.project_progress.dto.ActionContentDto;
import com.project.PJA.project_progress.service.ActionCommentService;
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
public class ActionCommentController {

    private final ActionCommentService actionCommentService;
    private final UserActionLogService userActionLogService;

    @PostMapping("{workspaceId}/project/action/{actionId}/post/{postId}/comment")
    public ResponseEntity<SuccessResponse<?>> createActionComment(@AuthenticationPrincipal Users user,
                                                                  @PathVariable("workspaceId") Long workspaceId,
                                                                  @PathVariable("actionId") Long actionId,
                                                                  @PathVariable("postId") Long postId,
                                                                  @RequestBody ActionContentDto dto) {
        Map<String, Object> data = actionCommentService.createActionComment(user, workspaceId, actionId, postId, dto);
        SuccessResponse<?> response = new SuccessResponse<>("success", "댓글을 생성했습니다.", data);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{workspaceId}/project/action/{actionId}/comment/{commentId}")
    public ResponseEntity<SuccessResponse<?>> updateActionComment(@AuthenticationPrincipal Users user,
                                                                  @PathVariable("workspaceId") Long workspaceId,
                                                                  @PathVariable("actionId") Long actionId,
                                                                  @PathVariable("commentId") Long commentId,
                                                                  @RequestBody ActionContentDto dto) {
        Map<String, Object> data = actionCommentService.updateActionComment(user, workspaceId, actionId, commentId, dto);
        SuccessResponse<?> response = new SuccessResponse<>("success", "댓글을 수정했습니다.", data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{workspaceId}/project/action/{actionId}/comment/{commentId}")
    public ResponseEntity<SuccessResponse<?>> deleteActionComment(@AuthenticationPrincipal Users user,
                                                                  @PathVariable("workspaceId") Long workspaceId,
                                                                  @PathVariable("actionId") Long actionId,
                                                                  @PathVariable("commentId") Long commentId) {
        actionCommentService.deleteActionComment(user, workspaceId, actionId, commentId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "댓글을 삭제했습니다.", null);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
