package com.project.PJA.workspace.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.security.service.CustomUserDetailService;
import com.project.PJA.workspace.dto.*;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    // 사용자의 워크스페이스 전체 조회
    @GetMapping("/")
    public ResponseEntity<SuccessResponse<List<WorkspaceResponse>>> getMyWorkspaces(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId;
        List<WorkspaceResponse> userWorkspaceList = workspaceService.getMyWorkspaces(userId);

        SuccessResponse<List<WorkspaceResponse>> response = new SuccessResponse<>(
                "success","워크스페이스 정보를 성공적으로 조회했습니다.", userWorkspaceList
        );

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 워크스페이스 생성
    @PostMapping("/")
    public ResponseEntity<SuccessResponse<WorkspaceResponse>> createWorkspace(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                              @RequestBody WorkspaceCreateRequest workspaceCreateRequest) {
        Long userId = userDetails.getUserId;
        WorkspaceResponse savedWorkspace = workspaceService.createWorkspace(userId, workspaceCreateRequest);

        SuccessResponse<WorkspaceResponse> response = new SuccessResponse<>(
                "success", "워크스페이스가 생성되었습니다.", savedWorkspace
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // 워크스페이스 수정
    @PutMapping("/{workspaceId}")
    public ResponseEntity<SuccessResponse<WorkspaceResponse>> updateWorkspace(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @PathVariable Long workspaceId,
                                @RequestBody WorkspaceUpdateRequest workspaceUpdateRequest) {
        Long userId = userDetails.getUserId;
        WorkspaceResponse updatedWorkspace = workspaceService.updateWorkspace(userId, workspaceId, workspaceUpdateRequest);

        SuccessResponse<WorkspaceResponse> response = new SuccessResponse<>(
                "success", "워크스페이스가 성공적으로 수정되었습니다.", updatedWorkspace
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 워크스페이스 진행도 완료 수정
    @PatchMapping("/{workspaceId}/complete")
    public ResponseEntity<SuccessResponse<WorkspaceResponse>> updateCompletionStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                     @PathVariable Long workspaceId,
                                                                                     @RequestBody WorkspaceProgressStep workspaceProgressStep) {
        Long userId = userDetails.getUserId;
        WorkspaceResponse updatedWorkspace = workspaceService.updateCompletionStatus(userId, workspaceId, workspaceProgressStep);

        SuccessResponse<WorkspaceResponse> response = new SuccessResponse<>(
                "success", "프로젝트가 성공적으로 수정되었습니다.", updatedWorkspace
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 워크스페이스 삭제
    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<SuccessResponse<WorkspaceResponse>> deleteWorkspace(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                              @PathVariable Long workspaceId) {
        Long userId = userDetails.getUserId;
        WorkspaceResponse deletedWorkspace = workspaceService.deleteWorkspace(userId, workspaceId);

        SuccessResponse<WorkspaceResponse> response = new SuccessResponse<>(
                "success", "워크스페이스가 성공적으로 삭제되었습니다.", deletedWorkspace
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 워크스페이스 팀원 초대
    @PostMapping("/{workspaceId}/invite")
    public ResponseEntity<SuccessResponse<Void>> inviteUserToWorkspace(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                       @PathVariable Long workspaceId,
                                                                       @RequestBody WorkspaceInviteRequest workspaceInviteRequest) {
        Long userId = userDetails.getUserId;
        workspaceService.sendInvitation(userId, workspaceId, workspaceInviteRequest);
    }
}
