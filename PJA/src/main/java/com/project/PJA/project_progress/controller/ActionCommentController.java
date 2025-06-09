package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.project_progress.dto.ActionCommentDto;
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
@RequestMapping("/api/workspace/")
@RequiredArgsConstructor
public class ActionCommentController {

    private final ActionCommentService actionCommentService;

    @PostMapping("{workspaceId}/project/action/{actionId}/post/{postId}/comment")
    public ResponseEntity<SuccessResponse<?>> createActionComment(@AuthenticationPrincipal Users user,
                                                                  @PathVariable("workspaceId") Long workspaceId,
                                                                  @PathVariable("actionId") Long actionId,
                                                                  @PathVariable("postId") Long postId,
                                                                  @RequestBody ActionCommentDto dto) {
        Map<String, Object> data = actionCommentService.createActionComment(user, workspaceId, actionId, postId, dto);
        SuccessResponse<?> response = new SuccessResponse<>("success", "댓글을 생성했습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    @PatchMapping("{workspaceId}/project/action/{actionId}/post/{postId}/comment/{commentId}")
//    public ResponseEntity<SuccessResponse<?>> updateActionComment(@AuthenticationPrincipal Users user,
//                                                                  @PathVariable("workspaceId") Long workspaceId,
//                                                                  @PathVariable("actionId") Long actionId,
//                                                                  @PathVariable("postId") Long postId,
//                                                                  @PathVariable("commentId") Long commentId,
//                                                                  @RequestBody ActionCommentDto dto) {
//        Map<String, Object> data = actionCommentService.createActionComment(user, workspaceId, actionId, postId, dto);
//        SuccessResponse<?> response = new SuccessResponse<>("success", "댓글을 생성했습니다.", data);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
}
