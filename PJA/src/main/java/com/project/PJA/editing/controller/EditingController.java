package com.project.PJA.editing.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.editing.dto.EditingRequest;
import com.project.PJA.editing.dto.EditingResponse;
import com.project.PJA.editing.service.EditingService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
