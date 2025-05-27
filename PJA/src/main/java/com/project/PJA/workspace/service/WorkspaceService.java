package com.project.PJA.workspace.service;

import com.project.PJA.workspace.dto.WorkspaceResponse;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    //private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    // 사용자의 전체 워크스페이스 조회
    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getMyWorkspaces(Long userId) {
        List<Workspace> userWorkspaces = workspaceRepository.findAllByUserId(userId);
        List<WorkspaceResponse> userWorkspaceList = userWorkspaces.stream()
                .map(workspace -> new WorkspaceResponse(workspace.getWorkspaceId(), workspace.getProjectName(), workspace.getTeamName(), workspace.getIsCompleted()))
                .collect(Collectors.toList());
        return userWorkspaceList;
    }

    @Transactional
    public void createWorkspace() {

    }
}
