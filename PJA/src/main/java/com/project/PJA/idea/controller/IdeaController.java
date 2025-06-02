package com.project.PJA.idea.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.idea.dto.ProjectInfoRequest;
import com.project.PJA.idea.dto.WorkspaceIdeaRequest;
import com.project.PJA.idea.dto.WorkspaceIdeaResponse;
import com.project.PJA.idea.service.IdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IdeaController {
    private final IdeaService ideaService;
    
    // 아이디어 조회
    @GetMapping("/{workspaceId}/prject-info")
    public ResponseEntity<SuccessResponse<WorkspaceIdeaResponse>> getIdea(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                          @PathVariable Long workspaceId) {
        Long userId = userDetails.getUserId;
        WorkspaceIdeaResponse workspaceIdea = ideaService.getIdea(userId, workspaceId);

        SuccessResponse<WorkspaceIdeaResponse> response = new SuccessResponse<>(
                "success", "아이디어 요약을 성공적으로 조회했습니다.", workspaceIdea
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 아이디어 수정
    @PutMapping("/{workspaceId}/prject-info")
    public ResponseEntity<SuccessResponse<WorkspaceIdeaResponse>> updateIdea(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                             @PathVariable Long workspaceId,
                                                                             @RequestBody WorkspaceIdeaRequest workspaceIdeaRequest) {
        Long userId = userDetails.getUserId;
        WorkspaceIdeaResponse updatedIdea = ideaService.updateIdea(userId, workspaceId, workspaceIdeaRequest);

        SuccessResponse<WorkspaceIdeaResponse> response = new SuccessResponse<>(
                "success", "아이디어 요약이 성공적으로 수정되었습니다.", updatedIdea
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 아이디어 삭제
    /*@DeleteMapping("/{workspaceId}/prject-info")
    public ResponseEntity<SuccessResponse<Void>> deleteIdea(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable Long workspaceId) {
        Long userId = userDetails.getUserId;
    }*/

    // 아이디어 ai 생성
    @PostMapping("/{workspaceId}/prject-info")
    public ResponseEntity<SuccessResponse<ProjectInfoRequest>> createIdea(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                          @PathVariable Long workspaceId,
                                                                          @RequestBody ProjectInfoRequest projectInfoRequest) {
        Long userId = userDetails.getUserId;
        ProjectInfoRequest projectInfo = ideaService.createIdea(userId, workspaceId, projectInfoRequest);

        SuccessResponse<ProjectInfoRequest> response = new SuccessResponse<>(
                "success", "아이디어 요약을 성공적으로 반환했습니다.", projectInfo
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{workspaceId}/project-info")
    public ResponseEntity<SuccessResponse<ProjectInfoRequest>> saveIdea(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @PathVariable Long workspaceId,
                                                          @RequestBody ProjectInfoRequest projectInfoRequest) {
        Long userId = userDetails.getUserId;
        ProjectInfoRequest projectInfo = ideaService.saveIdea(userId, workspaceId, projectInfoRequest);

        SuccessResponse<ProjectInfoRequest> response = new SuccessResponse<>(
                "success", "아이디어 요약이 성공적으로 저장되었습니다.", projectInfo
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
