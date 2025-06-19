package com.project.PJA.requirement.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.user_act_log.service.UserActionLogService;
import com.project.PJA.requirement.dto.RequirementContentRequest;
import com.project.PJA.requirement.dto.RequirementRequest;
import com.project.PJA.requirement.dto.RequirementResponse;
import com.project.PJA.requirement.service.RequirementService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class RequirementController {
    private final RequirementService requirementService;
    private final UserActionLogService userActionLogService;

    // 요구사항 명세서 조회
    @GetMapping("/{workspaceId}/requirements")
    public ResponseEntity<SuccessResponse<List<RequirementResponse>>> getRequirement(@AuthenticationPrincipal Users user,
                                                                                     @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        log.info("=== 요구사항 명세서 조회 API 진입 == userId: {}", userId);
        
        List<RequirementResponse> requirementResponse = requirementService.getRequirement(userId, workspaceId);

        SuccessResponse<List<RequirementResponse>> response = new SuccessResponse<>(
                "success", "요구사항 명세서가 성공적으로 조회되었습니다.", requirementResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 요구사항 명세서 AI 추천 요청
    @PostMapping("/{workspaceId}/requirements/recommendations")
    public ResponseEntity<SuccessResponse<List<RequirementRequest>>> recommendRequirement(@AuthenticationPrincipal Users user,
                                                                                          @PathVariable Long workspaceId,
                                                                                          @RequestBody List<RequirementRequest> requirementRequests) {
        Long userId = user.getUserId();
        log.info("=== 요구사항 명세서  AI 추천 요청 API 진입 == userId: {}", userId);

        List<RequirementRequest> recommendRequirements = requirementService.recommendRequirement(userId, workspaceId, requirementRequests);

        SuccessResponse<List<RequirementRequest>> response = new SuccessResponse<>(
                "success", "AI로부터 요구사항 추천을 성공적으로 받았습니다.", recommendRequirements
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 요구사항 명세서 생성
    @PostMapping("/{workspaceId}/requirements")
    public ResponseEntity<SuccessResponse<RequirementResponse>> createRequirement(@AuthenticationPrincipal Users user,
                                                                                  @PathVariable Long workspaceId,
                                                                                  @RequestBody RequirementRequest requirementRequest) {
        Long userId = user.getUserId();
        log.info("=== 요구사항 명세서 생성 API 진입 == userId: {}", userId);
        
        RequirementResponse requirementResponse = requirementService.createRequirement(userId, workspaceId, requirementRequest);

        SuccessResponse<RequirementResponse> response = new SuccessResponse<>(
                "success", "요구사항이 성공적으로 저장되었습니다.", requirementResponse
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 요구사항 명세서 수정
    @PutMapping("/{workspaceId}/requirements/{requirementId}")
    public ResponseEntity<SuccessResponse<RequirementResponse>> updateRequirement(@AuthenticationPrincipal Users user,
                                                                                  @PathVariable Long workspaceId,
                                                                                  @PathVariable Long requirementId,
                                                                                  @RequestBody RequirementContentRequest requirementContentRequest) {
        Long userId = user.getUserId();
        log.info("=== 요구사항 명세서 수정 API 진입 == userId: {}", userId);
        
        RequirementResponse requirementResponse = requirementService.updateRequirement(userId, workspaceId, requirementId, requirementContentRequest);

        SuccessResponse<RequirementResponse> response = new SuccessResponse<>(
                "success", "요구사항이 성공적으로 수정되었습니다.", requirementResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 요구사항 명세서 삭제
    @DeleteMapping("/{workspaceId}/requirements/{requirementId}")
    public ResponseEntity<SuccessResponse<RequirementResponse>> deleteRequirement(@AuthenticationPrincipal Users user,
                                                                                  @PathVariable Long workspaceId,
                                                                                  @PathVariable Long requirementId) {
        Long userId = user.getUserId();
        log.info("=== 요구사항 명세서 삭제 API 진입 == userId: {}", userId);
        
        RequirementResponse requirementResponse = requirementService.deleteRequirement(userId, workspaceId, requirementId);

        SuccessResponse<RequirementResponse> response = new SuccessResponse<>(
                "success", "요구사항이 성공적으로 삭제되었습니다.", requirementResponse
        );


        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
