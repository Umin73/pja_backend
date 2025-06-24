package com.project.PJA.editing.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.editing.dto.EditingRequest;
import com.project.PJA.editing.dto.EditingResponse;
import com.project.PJA.editing.dto.IdeaInputEditingRequest;
import com.project.PJA.editing.service.EditingService;
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
@RequestMapping("/api/editing")
public class EditingController {
    private final EditingService editingService;

    // 편집 시작
    @PostMapping("/{workspaceId}/start")
    public ResponseEntity<SuccessResponse<EditingResponse>> startEditing(@AuthenticationPrincipal Users user,
                                                                         @PathVariable Long workspaceId,
                                                                         @RequestBody EditingRequest editingRequest) {
        Long userId = user.getUserId();
        String userName = user.getName();
        EditingResponse editingResponse = editingService.startEditing(userId, userName, workspaceId, editingRequest);

        SuccessResponse<EditingResponse> response = new SuccessResponse<>(
                "success", "편집을 시작했습니다.", editingResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 편집 유지
    @PostMapping("/{workspaceId}/keep")
    public ResponseEntity<SuccessResponse<EditingResponse>> keepEditing(@AuthenticationPrincipal Users user,
                                                                        @PathVariable Long workspaceId,
                                                                        @RequestBody EditingRequest editingRequest) {
        Long userId = user.getUserId();
        String userName = user.getName();
        EditingResponse editingResponse = editingService.keepEditing(userId, userName, workspaceId, editingRequest);

        SuccessResponse<EditingResponse> response = new SuccessResponse<>(
                "success", "편집을 유지합니다.", editingResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 편집 삭제
    @PostMapping("/{workspaceId}/stop")
    public ResponseEntity<SuccessResponse<EditingResponse>> stopEditing(@AuthenticationPrincipal Users user,
                                                                        @PathVariable Long workspaceId,
                                                                        @RequestBody EditingRequest editingRequest) {
        Long userId = user.getUserId();
        String userName = user.getName();
        EditingResponse editingResponse = editingService.stopEditing(userId, userName, workspaceId, editingRequest);

        SuccessResponse<EditingResponse> response = new SuccessResponse<>(
                "success", "편집을 종료합니다.", editingResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 편집 상태 조회
    @GetMapping("/{workspaceId}/{page}")
    public ResponseEntity<SuccessResponse<List<EditingResponse>>> getEditingStatus(@AuthenticationPrincipal Users user,
                                                                                   @PathVariable Long workspaceId,
                                                                                   @PathVariable String page) {
        Long userId = user.getUserId();
        List<EditingResponse> editingResponses = editingService.getEditingStatus(userId, workspaceId, page);

        SuccessResponse<List<EditingResponse>> response = new SuccessResponse<>(
                "success", "편집 중인 페이지를 조회합니다.", editingResponses
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 아이디어 입력 편집 시작
    @PostMapping("/{workspaceId}/idea-input/start")
    public ResponseEntity<SuccessResponse<Void>> startIdeaInputEditing(@AuthenticationPrincipal Users user,
                                                                       @PathVariable Long workspaceId,
                                                                       @PathVariable IdeaInputEditingRequest request) {
        Long userId = user.getUserId();
        String userName = user.getName();
        String userProfile = user.getProfileImage();
        log.info("=== 아이디어 입력 편집 시작 API 진입 == userId: {}", userId);

        editingService.startIdeaInputEditing(userId, userName, userProfile, workspaceId, request);

        SuccessResponse<Void> response = new SuccessResponse<>(
                "success", "아이디어 입력 편집을 시작합니다.", null
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
