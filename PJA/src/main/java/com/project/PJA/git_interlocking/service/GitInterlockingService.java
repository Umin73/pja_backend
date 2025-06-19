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

    public GitInterlockingService(WorkspaceService workspaceService, GitInterlockingRepository gitRepository, WorkspaceRepository workspaceRepository) {
        this.workspaceService = workspaceService;
        this.gitRepository = gitRepository;
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional
    public String createGit(Users user, Long workspaceId, GitInfoDto dto) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                        .orElseThrow(() -> new NotFoundException("워크스페이스가 발견되지 않았습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "깃허브 정보를 생성할 권한이 없습니다.");

        Optional<GitInterlocking> optionalGit = gitRepository.findByWorkspace_WorkspaceId(workspaceId);

        if (optionalGit.isPresent()) {
            throw new ConflictException("Git이 이미 생성되어 있습니다.");
        }
        GitInterlocking gitInterlocking = new GitInterlocking();
        gitInterlocking.setWorkspace(foundWorkspace);
        gitInterlocking.setGitUrl(dto.getUrl());

        gitRepository.save(gitInterlocking);

        return gitInterlocking.getGitUrl();
    }

    @Transactional(readOnly = true)
    public String getGitUrl(Users user, Long workspaceId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "깃허브 정보를 조회할 권한이 없습니다.");

        Optional<GitInterlocking> optionalGit = gitRepository.findByWorkspace_WorkspaceId(workspaceId);
        if(optionalGit.isEmpty()) {
            throw new NotFoundException("워크스페이스 아이디로 발견된 Git 정보가 없습니다.");
        }

        return optionalGit.get().getGitUrl();
    }

    @Transactional
    public String updateGitInfo(Users user, Long workspaceId, GitInfoDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "깃허브 url을 설정할 권한이 없습니다.");

        Optional<GitInterlocking> optionalGit = gitRepository.findByWorkspace_WorkspaceId(workspaceId);

        if(optionalGit.isEmpty()) {
            throw new NotFoundException("워크스페이스 아이디로 발견된 Git 정보가 없습니다.");
        }
        GitInterlocking git = optionalGit.get();
        git.setGitUrl(dto.getUrl());

        return gitRepository.save(git).getGitUrl();
    }
}
