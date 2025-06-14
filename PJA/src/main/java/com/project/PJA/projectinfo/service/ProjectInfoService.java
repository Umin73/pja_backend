package com.project.PJA.projectinfo.service;

import com.project.PJA.exception.NotFoundException;
import com.project.PJA.projectinfo.dto.*;
import com.project.PJA.projectinfo.entity.ProjectInfo;
import com.project.PJA.projectinfo.repository.ProjectInfoRepository;
import com.project.PJA.workspace.entity.Workspace;
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
public class ProjectInfoService {
    private final WorkspaceRepository workspaceRepository;
    private final ProjectInfoRepository projectInfoRepository;
    private final RestTemplate restTemplate;
    private final WorkspaceService workspaceService;

    // 프로젝트 정보 조회
    @Transactional(readOnly = true)
    public ProjectInfoResponse getProjectInfo(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.validateWorkspaceAccess(userId, foundWorkspace);

        ProjectInfo foundProjectInfo = projectInfoRepository.findByWorkspace_WorkspaceId(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 아이디어를 찾을 수 없습니다."));

        return new ProjectInfoResponse(
                foundProjectInfo.getProjectInfoId(),
                foundProjectInfo.getTitle(),
                foundProjectInfo.getCategory(),
                foundProjectInfo.getTargetUsers(),
                foundProjectInfo.getCoreFeatures(),
                foundProjectInfo.getTechnologyStack(),
                foundProjectInfo.getProblemSolving()
        );
    }

    // 프로젝트 정보 AI 생성 -> ML에서 주는 데이터에 맞게 수정 / 프론트한테 받는 값 없고 db에서 가져와서 보내기
    public ProjectInfoRequest createProjectInfo(Long userId, Long workspaceId, ProjectInfoRequest request) {
        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

        //
        // MLOps URL 설정
        String mlopsUrl = "http://{mlops-domain.com}/mlops/models/project-info/generate";

        try {
            ResponseEntity<MlProjectInfoResponse> response = restTemplate.postForEntity(
                    mlopsUrl,
                    request,
                    MlProjectInfoResponse.class);

            MlProjectInfoResponse body = response.getBody();

            MlProblemSolving mlProblemSolving = body.getProblemSolving();
            ProblemSolving converted = ProblemSolving.builder()
                    .currentProblem(mlProblemSolving.getCurrentProblem())
                    .solutionIdea(mlProblemSolving.getSolutionIdea())
                    .expectedBenefits(mlProblemSolving.getExpectedBenefits())
                    .build();
            
            // DB에 바로 저장하게 코드 추가

            return new ProjectInfoRequest(
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

    // 프로젝트 정보 저장 -> 삭제 예정(필요없음)
    @Transactional
    public ProjectInfoResponse saveProjectInfo(Long userId, Long workspaceId, ProjectInfoRequest request) {
        // 워크스페이스 확인
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자가 오너가 아니면 403 반환
        workspaceService.authorizeOwnerOrThrow(userId, foundWorkspace, "아이디어 요약을 저장할 권한이 없습니다.");

        // 레파지토리에 저장
        ProjectInfo savedProjectInfo = projectInfoRepository.save(ProjectInfo.builder()
                .workspace(foundWorkspace)
                .title(request.getTitle())
                .category(request.getCategory())
                .targetUsers(request.getTargetUsers())
                .coreFeatures(request.getCoreFeatures())
                .technologyStack(request.getTechnologyStack())
                .problemSolving(request.getProblemSolving())
                .build());

        return new ProjectInfoResponse(
                savedProjectInfo.getProjectInfoId(),
                savedProjectInfo.getTitle(),
                savedProjectInfo.getCategory(),
                savedProjectInfo.getTargetUsers(),
                savedProjectInfo.getCoreFeatures(),
                savedProjectInfo.getTechnologyStack(),
                savedProjectInfo.getProblemSolving()
        );
    }

    // 프로젝트 정보 수정
    @Transactional
    public ProjectInfoResponse updateProjectInfo(Long userId, Long workspaceId, Long projectInfoId, ProjectInfoRequest request) {
        // 수정 권한 확인(멤버 or 오너)
        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 수정할 권한이 없습니다.");

        // 맞으면 수정 가능
        ProjectInfo foundProjectInfo = projectInfoRepository.findById(projectInfoId)
                .orElseThrow(() -> new NotFoundException("요청하신 아이디어 요약을 찾을 수 없습니다."));

        foundProjectInfo.update(
                request.getTitle(),
                request.getCategory(),
                request.getTargetUsers(),
                request.getCoreFeatures(),
                request.getTechnologyStack(),
                request.getProblemSolving()
                );

        return new ProjectInfoResponse(
                projectInfoId,
                request.getTitle(),
                request.getCategory(),
                request.getTargetUsers(),
                request.getCoreFeatures(),
                request.getTechnologyStack(),
                request.getProblemSolving()
        );
    }
}
