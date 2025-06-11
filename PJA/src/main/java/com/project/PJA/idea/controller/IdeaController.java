package com.project.PJA.idea.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.idea.dto.ProjectInfoRequest;
import com.project.PJA.idea.dto.ProjectSummaryReponse;
import com.project.PJA.idea.dto.ProjectSummaryRequest;
import com.project.PJA.idea.service.IdeaService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class IdeaController {
    private final IdeaService ideaService;

    // 실시간 조회를 위한 캐시에서 아이디어 요약 조회
    @GetMapping("/{workspaceId}/project-info/cache")
    public ResponseEntity<SuccessResponse<ProjectSummaryRequest>> getIdeaFromCache(@AuthenticationPrincipal Users user,
                                                                                   @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        ProjectSummaryRequest redisIdea = ideaService.getIdeaFromCache(userId, workspaceId);

        SuccessResponse<ProjectSummaryRequest> response = new SuccessResponse<>(
                "success", "임시 저장된 아이디어 요약을 조회했습니다.", redisIdea
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 실시간 조회를 위한 캐시에서 아이디어 요약 저장
    @PutMapping("/{workspaceId}/project-info/cache")
    public ResponseEntity<SuccessResponse<Void>> updateIdeaFromCache(@AuthenticationPrincipal Users user,
                                                                     @PathVariable Long workspaceId,
                                                                     @RequestBody ProjectSummaryRequest projectSummaryRequest) {
        Long userId = user.getUserId();
        ideaService.updateIdeaFromCache(userId, workspaceId, projectSummaryRequest);

        SuccessResponse<Void> response = new SuccessResponse<>(
                "success", "아이디어 요약이 임시 저장되었습니다.", null
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // 아이디어 조회
    @GetMapping("/{workspaceId}/project-info")
    public ResponseEntity<SuccessResponse<ProjectSummaryReponse>> getIdea(@AuthenticationPrincipal Users user,
                                                                          @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        ProjectSummaryReponse idea = ideaService.getIdea(userId, workspaceId);

        SuccessResponse<ProjectSummaryReponse> response = new SuccessResponse<>(
                "success", "아이디어 요약을 성공적으로 조회했습니다.", idea
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 아이디어 ai 생성
    @PostMapping("/{workspaceId}/project-info/generate")
    public ResponseEntity<SuccessResponse<ProjectSummaryRequest>> createIdea(@AuthenticationPrincipal Users user,
                                                                             @PathVariable Long workspaceId,
                                                                             @RequestBody ProjectInfoRequest projectInfoRequest) {
        Long userId = user.getUserId();
        ProjectSummaryRequest createProjectSummaryByAI = ideaService.createIdea(userId, workspaceId, projectInfoRequest);

        SuccessResponse<ProjectSummaryRequest> response = new SuccessResponse<>(
                "success", "아이디어 요약을 성공적으로 반환했습니다.", createProjectSummaryByAI
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ai가 생성한 아이디어 저장
    @PostMapping("/{workspaceId}/project-info/summary")
    public ResponseEntity<SuccessResponse<ProjectSummaryReponse>> saveIdea(@AuthenticationPrincipal Users user,
                                                                           @PathVariable Long workspaceId,
                                                                           @RequestBody ProjectSummaryRequest projectSummary) {
        Long userId = user.getUserId();
        ProjectSummaryReponse saveProjectSummary = ideaService.saveIdea(userId, workspaceId, projectSummary);

        SuccessResponse<ProjectSummaryReponse> response = new SuccessResponse<>(
                "success", "아이디어 요약이 성공적으로 저장되었습니다.", saveProjectSummary
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 아이디어 수정(동시 수정 불가능하게 바꾸기 or 동시에 실시간으로 수정 가능하게 바꾸기)
    @PutMapping("/{workspaceId}/project-info/{ideaId}")
    public ResponseEntity<SuccessResponse<ProjectSummaryReponse>> updateIdea(@AuthenticationPrincipal Users user,
                                                                             @PathVariable Long workspaceId,
                                                                             @PathVariable Long ideaId,
                                                                             @RequestBody ProjectSummaryRequest projectSummaryRequest) {
        Long userId = user.getUserId();
        ProjectSummaryReponse updatedIdea = ideaService.updateIdea(userId, workspaceId, ideaId, projectSummaryRequest);

        SuccessResponse<ProjectSummaryReponse> response = new SuccessResponse<>(
                "success", "아이디어 요약이 성공적으로 수정되었습니다.", updatedIdea
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
