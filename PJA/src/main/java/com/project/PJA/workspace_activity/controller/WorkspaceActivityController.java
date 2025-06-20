package com.project.PJA.workspace_activity.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace_activity.dto.WorkspaceActivityResponseDto;
import com.project.PJA.workspace_activity.service.WorkspaceActivityService;
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
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceActivityController {

    private final WorkspaceActivityService workspaceActivityService;

    @GetMapping("/{workspaceId}/workspace-activity}")
    ResponseEntity<SuccessResponse<?>> getWorkspaceActivity(@AuthenticationPrincipal Users user,
                                                            @PathVariable Long workspaceId) {

        List<WorkspaceActivityResponseDto> data = workspaceActivityService.getWorkspaceActivities(user, workspaceId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "워크스페이스 최근 활동 조회에 성공하였습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
