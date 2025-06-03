package com.project.PJA.idea.service;

import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.idea.dto.ProjectInfoRequest;
import com.project.PJA.idea.dto.ProjectSummaryRequest;
import com.project.PJA.idea.entity.Idea;
import com.project.PJA.idea.repository.IdeaRepository;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.enumeration.WorkspaceRole;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class IdeaService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final IdeaRepository ideaRepository;
    private final RestTemplate restTemplate;

    // 아이디어 조회
    @Transactional(readOnly = true)
    public Idea getIdea(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 비공개인데 멤버가 아니면 403 반환
        if (!foundWorkspace.getIsPublic()) {
            boolean isMember = workspaceMemberRepository.existsByWorkspaceIdAndUser_UserId(workspaceId, userId);
            if(!isMember) {
                throw new ForbiddenException("이 워크스페이스에 접근할 권한이 없습니다.");
            }
        }

        return ideaRepository.findByWorkspaceId(workspaceId);
    }

    // 아이디어 ai 생성
    public ProjectSummaryRequest createIdea(Long userId, Long workspaceId, ProjectInfoRequest projectInfo) {
        // 워크스페이스 확인
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자 확인
        if(!foundWorkspace.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("아이디어 요약을 요청할 권한이 없습니다.");
        }

        // MLOps URL 설정
        String mlopsUrl = "http://{mlops-domain.com}/mlops/models/project-info/generate";

        try {
            ResponseEntity<ProjectSummaryRequest> response = restTemplate.postForEntity(
                    mlopsUrl,
                    projectInfo,
                    ProjectSummaryRequest.class);

            return response.getBody();
        }
        catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("MLOps API 호출 실패: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    // 아이디어 저장
    @Transactional
    public ProjectSummaryRequest saveIdea(Long userId, Long workspaceId, ProjectSummaryRequest projectSummaryRequest) {
        // 워크스페이스 확인
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자가 멤버이거나 오너가 아니면 403 반환
        if(foundWorkspace.getUser().getUserId() != userId) {
            throw new ForbiddenException("아이디어 요약을 요청할 권한이 없습니다.");
        }

        // 레파지토리에 저장
        //foundWorkspace.update(projectInfoRequest.getProjectDescription());

        return projectSummaryRequest;
    }

    // 아이디어 수정
    @Transactional
    public ProjectSummaryRequest updateIdea(Long userId, Long workspaceId, ProjectSummaryRequest projectSummaryRequest) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 비공개인데 멤버가 아니면 403 반환
        if (!foundWorkspace.getIsPublic()) {
            boolean isMember = workspaceMemberRepository.existsByWorkspaceIdAndUser_UserId(workspaceId, userId);
            if(!isMember) {
                throw new ForbiddenException("이 워크스페이스에 접근할 권한이 없습니다.");
            }
        }

        // 수정 권한 확인(멤버 or 오너)
        WorkspaceMember foundWorkspaceMember = workspaceMemberRepository.findByWorkspaceIdAndUser_UserId(workspaceId, userId);

        if (foundWorkspaceMember.getWorkspaceRole() != WorkspaceRole.OWNER &&
                foundWorkspaceMember.getWorkspaceRole() != WorkspaceRole.MEMBER) {
            throw new ForbiddenException("이 워크스페이스에 수정할 권한이 없습니다.");
        }

        // 맞으면 수정 가능
        //foundWorkspace.updateProjectSummary(workspaceIdeaRequest.getProjectSummary());

        //return new WorkspaceIdeaResponse(workspaceId, projectSummaryRequest);
        return projectSummaryRequest;
    }
}
