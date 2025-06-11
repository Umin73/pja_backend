package com.project.PJA.editing.controller;

import com.project.PJA.editing.dto.EditingRequest;
import com.project.PJA.editing.service.EditingService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/editing")
public class EditingController {
    private final EditingService editingService;

    // 편집 시작
    @PostMapping("/{workspaceId}/start")
    public void startEditing(@AuthenticationPrincipal Users user,
                             @PathVariable Long workspaceId,
                             @RequestBody EditingRequest editingRequest) {
        Long userId = user.getUserId();
        editingService.startEditing(userId, workspaceId, editingRequest);
    }

    // 편집 유지 keepEditing
    @PostMapping("/{workspaceId}/keep")
    public void keepEditing(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
    }

    // 편집 삭제 stopEditing
    @PostMapping("/{workspaceId}/stop")
    public void stopEditing(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
    }

    // 편집 상태 조회 getEditingStatus
    @GetMapping("/{workspaceId}")
    public void getEditingStatus(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
    }
}
