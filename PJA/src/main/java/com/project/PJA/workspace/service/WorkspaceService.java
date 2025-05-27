package com.project.PJA.workspace.service;

import com.project.PJA.workspace.dto.WorkspaceResponse;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    // 사용자의 전체 워크스페이스 조회
    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getMyWorkspaces(Long userId) {
        List<WorkspaceMember> foundworkspaceMembers = workspaceMemberRepository.findAllByUserId(userId);

        List<Long> workspaceIds = foundworkspaceMembers.stream()
                .map(workspace -> workspace.getWorkspace().getWorkspaceId())
                .collect(Collectors.toList());

        List<Workspace> foundWorkspaces = workspaceRepository.findAllById(workspaceIds);

        List<WorkspaceResponse> userWorkspaceList = foundWorkspaces.stream()
                .map(workspace -> new WorkspaceResponse(workspace.getWorkspaceId(), workspace.getProjectName(), workspace.getTeamName(), workspace.getUser().getUser_id(), workspace.getIsCompleted()))
                .collect(Collectors.toList());

        return userWorkspaceList;
    }

    @Transactional
    public void createWorkspace() {

    }
}
