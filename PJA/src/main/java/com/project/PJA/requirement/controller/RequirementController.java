package com.project.PJA.requirement.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.requirement.dto.RequirementContentRequest;
import com.project.PJA.requirement.dto.RequirementRequest;
import com.project.PJA.requirement.dto.RequirementResponse;
import com.project.PJA.requirement.service.RequirementService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspace")
public class RequirementController {
    private final RequirementService requirementService;

    // 요구사항 명세서 조회
    @GetMapping("/{workspaceId}/requirements")
    public ResponseEntity<SuccessResponse<List<RequirementResponse>>> getRequirement(@AuthenticationPrincipal Users user,
                                                                                     @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        List<RequirementResponse> requirementResponse = requirementService.getRequirement(userId, workspaceId);

        SuccessResponse<List<RequirementResponse>> response = new SuccessResponse<>(
                "success", "요구사항 명세서가 성공적으로 조회되었습니다.", requirementResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 요구사항 명세서 ai 생성 요청

    // 요구사항 명세서 저장
    @PostMapping("/{workspaceId}/requirements/confirm")
    public ResponseEntity<SuccessResponse<List<RequirementResponse>>> saveRequirement(@AuthenticationPrincipal Users user,
                                                                                @PathVariable Long workspaceId,
                                                                                @RequestBody List<RequirementRequest> requirementRequests) {
        Long userId = user.getUserId();
        List<RequirementResponse> requirementResponse = requirementService.saveRequirement(userId, workspaceId, requirementRequests);

        SuccessResponse<List<RequirementResponse>> response = new SuccessResponse<>(
                "success", "요구사항이 성공적으로 저장되었습니다.", requirementResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 요구사항 명세서 생성
    @PostMapping("/{workspaceId}/requirements")
    public ResponseEntity<SuccessResponse<RequirementResponse>> createRequirement(@AuthenticationPrincipal Users user,
                                                                                  @PathVariable Long workspaceId,
                                                                                  @RequestBody RequirementRequest requirementRequest) {
        Long userId = user.getUserId();
        RequirementResponse requirementResponse = requirementService.createRequirement(userId, workspaceId, requirementRequest);

        SuccessResponse<RequirementResponse> response = new SuccessResponse<>(
                "success", "요구사항이 성공적으로 저장되었습니다.", requirementResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 요구사항 명세서 수정
    @PutMapping("/{workspaceId}/requirements/{requirementId}")
    public ResponseEntity<SuccessResponse<RequirementResponse>> updateRequirement(@AuthenticationPrincipal Users user,
                                                                                  @PathVariable Long workspaceId,
                                                                                  @PathVariable Long requirementId,
                                                                                  @RequestBody RequirementContentRequest requirementContentRequest) {
        Long userId = user.getUserId();
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
        RequirementResponse requirementResponse = requirementService.deleteRequirement(userId, workspaceId, requirementId);

        SuccessResponse<RequirementResponse> response = new SuccessResponse<>(
                "success", "요구사항이 성공적으로 삭제되었습니다.", requirementResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
