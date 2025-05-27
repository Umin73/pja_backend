package com.project.PJA.workspace.controller;

import com.project.PJA.workspace.dto.WorkspaceResponse;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return;
    }

    // 워크스페이스 생성
    @PostMapping("/")
    public void createWorkspace() {
        Long userId = 0L;

    }
}
