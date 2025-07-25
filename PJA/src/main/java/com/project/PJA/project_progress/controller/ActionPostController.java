package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.user_act_log.service.UserActionLogService;
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
    public ResponseEntity<SuccessResponse<?>> readActionPost(@AuthenticationPrincipal Users user,
                                                            @PathVariable("actionId") Long actionId,
                                                             @PathVariable("postId") Long postId) {
        Map<String, Object> data = actionPostService.readActionPost(user, actionId, postId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "액션 게시글이 조회되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/project/action/{actionId}/post/{postId}")
    public ResponseEntity<SuccessResponse<?>> updateActionPost(@AuthenticationPrincipal Users user,
                                                           @PathVariable("workspaceId") Long workspaceId,
                                                           @PathVariable("actionId") Long actionId,
                                                           @PathVariable("postId") Long postId,
                                                           @RequestPart(value = "content", required = false) String content,
                                                           @RequestPart(value = "files", required = false) List<MultipartFile> fileList,
                                                           @RequestPart(value = "removedFilePaths", required = false) String removedFilePaths) throws IOException {
        Map<String, Object> data
                = actionPostService.updateActionPostContent
                (user, workspaceId, actionId, postId, content, fileList, removedFilePaths);

        SuccessResponse<?> response = new SuccessResponse<>("success", "액션이 게시글이 수정되었습니다.", data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
