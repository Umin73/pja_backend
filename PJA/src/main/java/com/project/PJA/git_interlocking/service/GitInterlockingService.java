package com.project.PJA.git_interlocking.service;

import com.project.PJA.exception.ConflictException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.git_interlocking.dto.GitInfoDto;
import com.project.PJA.git_interlocking.entity.GitInterlocking;
import com.project.PJA.git_interlocking.repository.GitInterlockingRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import com.project.PJA.workspace_activity.enumeration.ActivityActionType;
import com.project.PJA.workspace_activity.enumeration.ActivityTargetType;
import com.project.PJA.workspace_activity.service.WorkspaceActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class GitInterlockingService {

    private final WorkspaceService workspaceService;
    private final GitInterlockingRepository gitRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceActivityService workspaceActivityService;

    public GitInterlockingService(WorkspaceService workspaceService, GitInterlockingRepository gitRepository, WorkspaceRepository workspaceRepository, WorkspaceActivityService workspaceActivityService) {
        this.workspaceService = workspaceService;
        this.gitRepository = gitRepository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceActivityService = workspaceActivityService;
    }

    @Transactional(readOnly = true)
    public String getGitUrl(Users user, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("워크스페이스 아이디로 발견된 워크스페이스가 존재하지 않습니다."));

        workspaceService.validateWorkspaceAccess(user.getUserId(), foundWorkspace);

        String gitUrl = foundWorkspace.getGithubUrl();

        if (!isValidGitUrl(gitUrl)) {
            throw new IllegalArgumentException("유효한 GitHub 레포지토리 URL이 아닙니다.");
        }

        return gitUrl;
    }

    private boolean isValidGitUrl(String gitUrl) {
        String regex = "^https://github\\.com/[\\w.-]+/[\\w.-]+(?:\\.git)?/?$";
        return gitUrl.matches(regex);
    }
}
