package com.project.PJA.idea.service;

import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.idea.dto.*;
import com.project.PJA.idea.entity.Idea;
import com.project.PJA.idea.repository.IdeaRepository;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
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
    private final WorkspaceService workspaceService;

    // 아이디어 조회
    @Transactional(readOnly = true)
    public ProjectSummaryReponse getIdea(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 비공개인데 멤버가 아니면 403 반환
        if (!foundWorkspace.getIsPublic()) {
            boolean isMember = workspaceMemberRepository.existsByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, userId);
            if(!isMember) {
                throw new ForbiddenException("이 워크스페이스에 접근할 권한이 없습니다.");
            }
        }

        Idea foundIdea = ideaRepository.findByWorkspace_WorkspaceId(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 아이디어를 찾을 수 없습니다."));

        return new ProjectSummaryReponse(
                foundIdea.getProjectSummaryId(),
                workspaceId,
                foundIdea.getTitle(),
                foundIdea.getCategory(),
                foundIdea.getTargetUsers(),
                foundIdea.getCoreFeatures(),
                foundIdea.getTechnologyStack(),
                foundIdea.getProblemSolving()
        );
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
            ResponseEntity<AiProjectSummaryResponse> response = restTemplate.postForEntity(
                    mlopsUrl,
                    projectInfo,
                    AiProjectSummaryResponse.class);

            AiProjectSummaryResponse body = response.getBody();

            AiProblemSolving aiProblemSolving = body.getProblemSolving();
            ProblemSolving converted = ProblemSolving.builder()
                    .currentProblem(aiProblemSolving.getCurrentProblem())
                    .solutionIdea(aiProblemSolving.getSolutionIdea())
                    .expectedBenefits(aiProblemSolving.getExpectedBenefits())
                    .build();

            return new ProjectSummaryRequest(
                    body.getTitle(),
                    body.getCategory(),
                    body.getTargetUsers(),
                    body.getCoreFeatures(),
                    body.getTechnologyStack(),
                    converted
            );
        }
        catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("MLOps API 호출 실패: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    // 아이디어 저장
    @Transactional
    public ProjectSummaryReponse saveIdea(Long userId, Long workspaceId, ProjectSummaryRequest projectSummary) {
        // 워크스페이스 확인
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자가 오너가 아니면 403 반환
        workspaceService.authorizeOwnerOrThrow(userId, workspaceId, "아이디어 요약을 저장할 권한이 없습니다.");

        // 레파지토리에 저장
        Idea savedIdea = ideaRepository.save(Idea.builder()
                .workspace(foundWorkspace)
                .title(projectSummary.getTitle())
                .category(projectSummary.getCategory())
                .targetUsers(projectSummary.getTargetUsers())
                .coreFeatures(projectSummary.getCoreFeatures())
                .technologyStack(projectSummary.getTechnologyStack())
                .problemSolving(projectSummary.getProblemSolving())
                .build());

        return new ProjectSummaryReponse(
                savedIdea.getProjectSummaryId(),
                workspaceId,
                savedIdea.getTitle(),
                savedIdea.getCategory(),
                savedIdea.getTargetUsers(),
                savedIdea.getCoreFeatures(),
                savedIdea.getTechnologyStack(),
                savedIdea.getProblemSolving()
        );
    }

    // 아이디어 수정
    @Transactional
    public ProjectSummaryReponse updateIdea(Long userId, Long workspaceId, Long ideaId, ProjectSummaryRequest projectSummary) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 비공개인데 멤버가 아니면 403 반환
        if (!foundWorkspace.getIsPublic()) {
            boolean isMember = workspaceMemberRepository.existsByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, userId);
            if(!isMember) {
                throw new ForbiddenException("이 워크스페이스에 접근할 권한이 없습니다.");
            }
        }

        // 수정 권한 확인(멤버 or 오너)
        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 수정할 권한이 없습니다.");

        // 맞으면 수정 가능
        Idea foundIdea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new NotFoundException("요청하신 아이디어 요약을 찾을 수 없습니다."));

        foundIdea.update(
                projectSummary.getTitle(),
                projectSummary.getCategory(),
                projectSummary.getTargetUsers(),
                projectSummary.getCoreFeatures(),
                projectSummary.getTechnologyStack(),
                projectSummary.getProblemSolving()
                );

        return new ProjectSummaryReponse(
                ideaId,
                workspaceId,
                projectSummary.getTitle(),
                projectSummary.getCategory(),
                projectSummary.getTargetUsers(),
                projectSummary.getCoreFeatures(),
                projectSummary.getTechnologyStack(),
                projectSummary.getProblemSolving()
        );
    }
}
