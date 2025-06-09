package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.project_progress.dto.ActionPostDto;
import com.project.PJA.project_progress.service.ActionPostService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/workspace/")
@RequiredArgsConstructor
public class ActionPostController {

    private final ActionPostService actionPostService;

    @PatchMapping("{workspaceId}/project/action/{actionId}/post/{postId}")
    public ResponseEntity<SuccessResponse<?>> updateActionPost(@AuthenticationPrincipal Users user,
                                                           @PathVariable("workspaceId") Long workspaceId,
                                                           @PathVariable("actionId") Long actionId,
                                                           @PathVariable("postId") Long postId,
                                                           @RequestBody ActionPostDto dto) {
        actionPostService.updateActionPostContent(user, workspaceId, actionId, postId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "액션이 게시글이 수정되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
