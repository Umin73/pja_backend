package com.project.PJA.idea.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.idea.dto.ProjectInfoRequest;
import com.project.PJA.idea.dto.ProjectSummaryRequest;
import com.project.PJA.idea.entity.Idea;
import com.project.PJA.idea.service.IdeaService;
import com.project.PJA.user.entity.Users;
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
    @GetMapping("/{workspaceId}/project-info")
    public ResponseEntity<SuccessResponse<Idea>> getIdea(@AuthenticationPrincipal Users user,
                                                         @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        Idea foundIdea = ideaService.getIdea(userId, workspaceId);

        SuccessResponse<Idea> response = new SuccessResponse<>(
                "success", "아이디어 요약을 성공적으로 조회했습니다.", foundIdea
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 아이디어 ai 생성
    @PostMapping("/{workspaceId}/project-info/ai")
    public ResponseEntity<SuccessResponse<ProjectSummaryRequest>> createIdea(@AuthenticationPrincipal Users user,
                                                                             @PathVariable Long workspaceId,
                                                                             @RequestBody ProjectInfoRequest projectInfoRequest) {
        Long userId = user.getUserId();
        ProjectSummaryRequest projectSummary = ideaService.createIdea(userId, workspaceId, projectInfoRequest);

        SuccessResponse<ProjectSummaryRequest> response = new SuccessResponse<>(
                "success", "아이디어 요약을 성공적으로 반환했습니다.", projectSummary
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ai가 생성한 아이디어 저장
    @PostMapping("/{workspaceId}/project-info/summary")
    public ResponseEntity<SuccessResponse<ProjectSummaryRequest>> saveIdea(@AuthenticationPrincipal Users user,
                                                                        @PathVariable Long workspaceId,
                                                                        @RequestBody ProjectSummaryRequest projectSummaryRequest) {
        Long userId = user.getUserId();
        ProjectSummaryRequest projectSummary = ideaService.saveIdea(userId, workspaceId, projectSummaryRequest);

        SuccessResponse<ProjectSummaryRequest> response = new SuccessResponse<>(
                "success", "아이디어 요약이 성공적으로 저장되었습니다.", projectSummary
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 아이디어 수정
    @PutMapping("/{workspaceId}/project-info")
    public ResponseEntity<SuccessResponse<ProjectSummaryRequest>> updateIdea(@AuthenticationPrincipal Users user,
                                                                             @PathVariable Long workspaceId,
                                                                             @RequestBody ProjectSummaryRequest projectSummaryRequest) {
        Long userId = user.getUserId();
        ProjectSummaryRequest updatedIdea = ideaService.updateIdea(userId, workspaceId, projectSummaryRequest);

        SuccessResponse<ProjectSummaryRequest> response = new SuccessResponse<>(
                "success", "아이디어 요약이 성공적으로 수정되었습니다.", updatedIdea
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
