package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.common.user_act_log.UserActionLogService;
import com.project.PJA.common.user_act_log.UserActionType;
import com.project.PJA.project_progress.service.ActionPostService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/workspaces/")
@RequiredArgsConstructor
public class ActionPostController {

    private final ActionPostService actionPostService;
    private final UserActionLogService userActionLogService;

    @GetMapping("{workspaceId}/project/action/{actionId}/post/{postId}")
    public ResponseEntity<SuccessResponse<?>> readActionPost(@PathVariable("actionId") Long actionId,
                                                             @PathVariable("postId") Long postId) {
        Map<String, Object> data = actionPostService.readActionPost(actionId, postId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "액션 게시글이 조회되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/project/action/{actionId}/post/{postId}")
    public ResponseEntity<SuccessResponse<?>> updateActionPost(@AuthenticationPrincipal Users user,
                                                           @PathVariable("workspaceId") Long workspaceId,
                                                           @PathVariable("actionId") Long actionId,
                                                           @PathVariable("postId") Long postId,
                                                           @RequestPart("content") String content,
                                                           @RequestPart(value = "files", required = false) List<MultipartFile> fileList) throws IOException {
        Map<String, Object> data = actionPostService.updateActionPostContent(user, workspaceId, actionId, postId, content, fileList);

        SuccessResponse<?> response = new SuccessResponse<>("success", "액션이 게시글이 수정되었습니다.", data);

        userActionLogService.log(
                UserActionType.UPDATE_PROJECT_PROGRESS_ACTION_POST,
                String.valueOf(user.getUserId()),
                user.getUsername(),
                workspaceId,
                Map.of(
                        "actionName", data.get("actionName"),
                        "content", data.get("content")
                )
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
