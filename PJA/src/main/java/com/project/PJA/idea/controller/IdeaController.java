package com.project.PJA.idea.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.idea.dto.ProjectInfoRequest;
import com.project.PJA.idea.dto.ProjectSummaryReponse;
import com.project.PJA.idea.dto.ProjectSummaryDto;
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
    public ResponseEntity<SuccessResponse<ProjectSummaryReponse>> getIdea(@AuthenticationPrincipal Users user,
                                                                          @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        ProjectSummaryReponse projectSummary = ideaService.getIdea(userId, workspaceId);

        SuccessResponse<ProjectSummaryReponse> response = new SuccessResponse<>(
                "success", "아이디어 요약을 성공적으로 조회했습니다.", projectSummary
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 아이디어 ai 생성
    @PostMapping("/{workspaceId}/project-info/generate")
    public ResponseEntity<SuccessResponse<ProjectSummaryDto>> createIdea(@AuthenticationPrincipal Users user,
                                                                         @PathVariable Long workspaceId,
                                                                         @RequestBody ProjectInfoRequest projectInfoRequest) {
        Long userId = user.getUserId();
        ProjectSummaryDto projectSummaryData = ideaService.createIdea(userId, workspaceId, projectInfoRequest);

        SuccessResponse<ProjectSummaryDto> response = new SuccessResponse<>(
                "success", "아이디어 요약을 성공적으로 반환했습니다.", projectSummaryData
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ai가 생성한 아이디어 저장
    @PostMapping("/{workspaceId}/project-info/summary")
    public ResponseEntity<SuccessResponse<ProjectSummaryReponse>> saveIdea(@AuthenticationPrincipal Users user,
                                                                           @PathVariable Long workspaceId,
                                                                           @RequestBody ProjectSummaryDto projectSummaryDtoRequest) {
        Long userId = user.getUserId();
        ProjectSummaryReponse projectSummary = ideaService.saveIdea(userId, workspaceId, projectSummaryDtoRequest);

        SuccessResponse<ProjectSummaryReponse> response = new SuccessResponse<>(
                "success", "아이디어 요약이 성공적으로 저장되었습니다.", projectSummary
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 아이디어 수정
    @PutMapping("/{workspaceId}/project-info/{ideaId}")
    public ResponseEntity<SuccessResponse<ProjectSummaryReponse>> updateIdea(@AuthenticationPrincipal Users user,
                                                                             @PathVariable Long workspaceId,
                                                                             @PathVariable Long ideaId,
                                                                             @RequestBody ProjectSummaryDto projectSummaryDto) {
        Long userId = user.getUserId();
        ProjectSummaryReponse updatedIdea = ideaService.updateIdea(userId, workspaceId, ideaId, projectSummaryDto);

        SuccessResponse<ProjectSummaryReponse> response = new SuccessResponse<>(
                "success", "아이디어 요약이 성공적으로 수정되었습니다.", updatedIdea
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
