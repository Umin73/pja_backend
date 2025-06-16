package com.project.PJA.projectinfo.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.common.user_act_log.UserActionLogService;
import com.project.PJA.common.user_act_log.UserActionType;
import com.project.PJA.projectinfo.dto.ProjectInfoResponse;
import com.project.PJA.projectinfo.dto.ProjectInfoRequest;
import com.project.PJA.projectinfo.service.ProjectInfoService;
import com.project.PJA.requirement.dto.RequirementRequest;
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
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class ProjectInfoController {
    private final ProjectInfoService projectInfoService;
    private final UserActionLogService userActionLogService;
    
    // 프로젝트 정보 조회
    @GetMapping("/{workspaceId}/project-info")
    public ResponseEntity<SuccessResponse<ProjectInfoResponse>> getProjectInfo(@AuthenticationPrincipal Users user,
                                                                               @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        log.info("=== 프로젝트 정보 조회 API 진입 == userId: {}", userId);
        
        ProjectInfoResponse projectInfo = projectInfoService.getProjectInfo(userId, workspaceId);

        SuccessResponse<ProjectInfoResponse> response = new SuccessResponse<>(
                "success", "프로젝트 정보를 성공적으로 조회했습니다.", projectInfo
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 프로젝트 정보 AI 생성
    @PostMapping("/{workspaceId}/project-info")
    public ResponseEntity<SuccessResponse<ProjectInfoResponse>> createProjectInfo(@AuthenticationPrincipal Users user,
                                                                                  @PathVariable Long workspaceId,
                                                                                  @RequestBody List<RequirementRequest> requirementRequests) {
        Long userId = user.getUserId();
        log.info("=== 프로젝트 정보 생성 API 진입 == userId: {}", userId);
        
        ProjectInfoResponse projectInfoResponse = projectInfoService.createProjectInfo(userId, workspaceId, requirementRequests);

        SuccessResponse<ProjectInfoResponse> response = new SuccessResponse<>(
                "success", "프로젝트 정보를 성공적으로 생성했습니다.", projectInfoResponse
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 프로젝트 정보 수정
    @PutMapping("/{workspaceId}/project-info/{projectInfoId}")
    public ResponseEntity<SuccessResponse<ProjectInfoResponse>> updateProjectInfo(@AuthenticationPrincipal Users user,
                                                                                  @PathVariable Long workspaceId,
                                                                                  @PathVariable Long projectInfoId,
                                                                                  @RequestBody ProjectInfoRequest projectInfoRequest) {
        Long userId = user.getUserId();
        log.info("=== 프로젝트 정보 수정 API 진입 == userId: {}", userId);
        
        ProjectInfoResponse projectInfo = projectInfoService.updateProjectInfo(userId, workspaceId, projectInfoId, projectInfoRequest);

        SuccessResponse<ProjectInfoResponse> response = new SuccessResponse<>(
                "success", "프로젝트 정보가 성공적으로 수정되었습니다.", projectInfo
        );

        userActionLogService.log(
                UserActionType.UPDATE_PROJECT_INFO,
                String.valueOf(userId),
                user.getUsername(),
                workspaceId,
                Map.of(
                        "projectInfo",projectInfo
                )
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
