package com.project.PJA.requirement.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.requirement.dto.RequirementResponse;
import com.project.PJA.requirement.service.RequirementService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspace")
public class RequirementController {
    private final RequirementService requirementService;

    // 요구사항 명세서 조회
    @GetMapping("/{workspaceId}/requirements")
    public ResponseEntity<SuccessResponse<RequirementResponse>> getRequirement(@AuthenticationPrincipal Users user,
                                                                               @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        RequirementResponse requirementResponse = requirementService.getRequirement(userId, workspaceId);

        SuccessResponse<RequirementResponse> response = new SuccessResponse<>(
                "success", "요구사항 명세서가 성공적으로 조회되었습니다.", requirementResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 요구사항 명세서 ai 생성 요청

    // 요구사항 명세서 저장
    
    // 요구사항 명세서 생성

    // 요구사항 명세서 수정

    // 요구사항 명세서 삭제
}
