package com.project.PJA.editing.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.editing.dto.EditingRequest;
import com.project.PJA.editing.dto.EditingResponse;
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
    private final ObjectMapper objectMapper;

    // 편집 시작
    @PostMapping("/{workspaceId}/start")
    public ResponseEntity<SuccessResponse<EditingResponse>> startEditing(@AuthenticationPrincipal Users user,
                                                                         @PathVariable Long workspaceId,
                                                                         @RequestBody EditingRequest editingRequest) {
        Long userId = user.getUserId();
        String userName = user.getName();
        String userProfile = user.getProfileImage();
        log.info("=== 편집 시작 API 진입 == userId: {}", userId);

        EditingResponse editingResponse = editingService.startEditing(userId, userName, userProfile, workspaceId, editingRequest);

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
        String userProfile = user.getProfileImage();
        log.info("=== 편집 유지 API 진입 == userId: {}", userId);

        EditingResponse editingResponse = editingService.keepEditing(userId, userName, userProfile, workspaceId, editingRequest);

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
        String userProfile = user.getProfileImage();
        log.info("=== 편집 삭제 API 진입 == userId: {}", userId);

        EditingResponse editingResponse = editingService.stopEditing(userId, userName, userProfile, workspaceId, editingRequest);

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
        log.info("=== 편집 조회 API 진입 == userId: {}", userId);
        log.info("=== 편집 조회 page: {}", page);
        List<EditingResponse> editingResponses = editingService.getEditingStatus(userId, workspaceId, page);

        SuccessResponse<List<EditingResponse>> response = new SuccessResponse<>(
                "success", "편집 중인 페이지를 조회합니다.", editingResponses
        );

        try {
            String dto = objectMapper.writeValueAsString(editingResponses);
            log.info("=== 편집 조회 data : {}", dto);
        } catch (JsonProcessingException e) {
            log.error("편집 조회 데이터 json 변화 실패", e);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
