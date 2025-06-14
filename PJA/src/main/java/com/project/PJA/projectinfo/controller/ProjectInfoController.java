package com.project.PJA.projectinfo.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.projectinfo.dto.ProjectInfoResponse;
import com.project.PJA.projectinfo.dto.ProjectInfoRequest;
import com.project.PJA.projectinfo.service.ProjectInfoService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class ProjectInfoController {
    private final ProjectInfoService projectInfoService;
    
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

    // 프로젝트 정보 AI 생성 -> 수정 예정(ProjectInfoRequest -> ProjectInfoResponse 바꾸기)
    @PostMapping("/{workspaceId}/project-info")
    public ResponseEntity<SuccessResponse<ProjectInfoRequest>> createProjectInfo(@AuthenticationPrincipal Users user,
                                                                                 @PathVariable Long workspaceId,
                                                                                 @RequestBody ProjectInfoRequest projectInfoRequest) {
        Long userId = user.getUserId();
        log.info("=== 프로젝트 정보 생성 API 진입 == userId: {}", userId);
        
        ProjectInfoRequest projectInfo = projectInfoService.createProjectInfo(userId, workspaceId, projectInfoRequest);

        SuccessResponse<ProjectInfoRequest> response = new SuccessResponse<>(
                "success", "아이디어 요약을 성공적으로 반환했습니다.", projectInfo
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ai가 생성한 아이디어 저장 -> 삭제 예정(필요없음)
    @PostMapping("/{workspaceId}/project-info/summary")
    public ResponseEntity<SuccessResponse<ProjectInfoResponse>> saveProjectInfo(@AuthenticationPrincipal Users user,
                                                                                @PathVariable Long workspaceId,
                                                                                @RequestBody ProjectInfoRequest projectInfoRequest) {
        Long userId = user.getUserId();
        log.info("=== 프로젝트 정보 저장 API 진입 == userId: {}", userId);
        
        ProjectInfoResponse projectInfo = projectInfoService.saveProjectInfo(userId, workspaceId, projectInfoRequest);

        SuccessResponse<ProjectInfoResponse> response = new SuccessResponse<>(
                "success", "아이디어 요약이 성공적으로 저장되었습니다.", projectInfo
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
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
                "success", "아이디어 요약이 성공적으로 수정되었습니다.", projectInfo
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
