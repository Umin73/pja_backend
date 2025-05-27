package com.project.PJA.workspace.controller;

import com.project.PJA.workspace.dto.WorkspaceCreateRequest;
import com.project.PJA.workspace.dto.WorkspaceResponse;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    // 사용자의 워크스페이스 전체 조회
    @GetMapping("/")
    public void getMyWorkspaces() {
        Long userId = 0L;
        List<WorkspaceResponse> userWorkspaceList = workspaceService.getMyWorkspaces(userId);
    }

    // 워크스페이스 생성
    @PostMapping("/")
    public void createWorkspace(@RequestBody WorkspaceCreateRequest workspaceCreateRequest) {
        Long userId = 0L;
        workspaceService.createWorkspace(userId, workspaceCreateRequest);
    }
}
