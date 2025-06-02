package com.project.PJA.idea.service;

import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.idea.dto.ProjectInfoRequest;
import com.project.PJA.idea.dto.WorkspaceIdeaRequest;
import com.project.PJA.idea.dto.WorkspaceIdeaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class IdeaService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final RestTemplate restTemplate;

    // 아이디어 조회
    @Transactional(readOnly = true)
    public WorkspaceIdeaResponse getIdea(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 비공개인데 멤버가 아니면 403 반환
        if (!foundWorkspace.getIsPublic()) {
            boolean isMember = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
            if(!isMember) {
                throw new ForbiddenException("이 워크스페이스에 접근할 권한이 없습니다.");
            }
        }

        return new WorkspaceIdeaResponse(workspaceId, foundWorkspace.getProjectSummary());
    }

    @Transactional
    public WorkspaceIdeaResponse updateIdea(Long userId, Long workspaceId, WorkspaceIdeaRequest workspaceIdeaRequest) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 비공개인데 멤버가 아니면 403 반환
        if (!foundWorkspace.getIsPublic()) {
            boolean isMember = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
            if(!isMember) {
                throw new ForbiddenException("이 워크스페이스에 접근할 권한이 없습니다.");
            }
        }

        // 수정 권한 확인(멤버 or 오너)
        WorkspaceMember foundWorkspaceMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new NotFoundException("해당 워크스페이스의 구성원이 아닙니다."));

        if (foundWorkspaceMember.getWorkspaceRole() != WorkspaceRole.OWNER &&
                foundWorkspaceMember.getWorkspaceRole != WorkspaceRole.MEMBER) {
            throw new ForbiddenException("이 워크스페이스에 수정할 권한이 없습니다.");
        }

        // 맞으면 수정 가능
        foundWorkspace.updateProjectSummary(workspaceIdeaRequest.getProjectSummary());

        return new WorkspaceIdeaResponse(workspaceId, workspaceIdeaRequest.getProjectSummary());
    }

    // 아이디어 삭제
    /*@Transactional
    public void deleteIdea(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));
    }*/

    // 아이디어 ai 생성
    public ProjectInfoRequest createIdea(Long userId, Long workspaceId, ProjectInfoRequest projectInfo) {
        // 워크스페이스 확인
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자 확인
        if(foundWorkspace.getUser.getUserId != userId) {
            throw new ForbiddenException("아이디어 요약을 요청할 권한이 없습니다.");
        }

        String mlopsUrl = "http://{mlops-domail}/api/mlops/models/project-info/generate";

        ResponseEntity<ProjectInfoRequest> response = restTemplate.postForEntity(mlopsUrl, projectInfo, ProjectInfoRequest.class);

        if(response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("MLOps API 호출 실패: " + response.getStatusCode());
        }
    }

    // 아이디어 저장
    @Transactional
    public ProjectInfoRequest saveIdea(Long userId, Long workspaceId, ProjectInfoRequest projectInfoRequest) {
        // 워크스페이스 확인
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자 확인
        if(foundWorkspace.getUser().getUserId() != userId) {
            throw new ForbiddenException("아이디어 요약을 요청할 권한이 없습니다.");
        }

        // 레파지토리에 저장
        foundWorkspace.update(projectInfoRequest.getProjectDescription());
        return projectInfoRequest;
    }
}
