package com.project.PJA.projectinfo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.exception.BadRequestException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.ideainput.dto.IdeaInputRequest;
import com.project.PJA.ideainput.dto.MainFunctionData;
import com.project.PJA.ideainput.dto.TechStackData;
import com.project.PJA.ideainput.entity.IdeaInput;
import com.project.PJA.ideainput.entity.MainFunction;
import com.project.PJA.ideainput.entity.TechStack;
import com.project.PJA.ideainput.repository.IdeaInputRepository;
import com.project.PJA.ideainput.repository.MainFunctionRepository;
import com.project.PJA.ideainput.repository.TechStackRepository;
import com.project.PJA.projectinfo.dto.*;
import com.project.PJA.projectinfo.entity.ProjectInfo;
import com.project.PJA.projectinfo.repository.ProjectInfoRepository;
import com.project.PJA.requirement.dto.RequirementRequest;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectInfoService {
    private final WorkspaceRepository workspaceRepository;
    private final ProjectInfoRepository projectInfoRepository;
    private final IdeaInputRepository ideaInputRepository;
    private final MainFunctionRepository mainFunctionRepository;
    private final TechStackRepository techStackRepository;
    private final RestTemplate restTemplate;
    private final WorkspaceService workspaceService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    //
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

    // 프로젝트 정보 AI 생성
    @Transactional
    public ProjectInfoResponse createProjectInfo(Long userId, Long workspaceId, List<RequirementRequest> requests) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

        if (projectInfoRepository.existsByWorkspace_WorkspaceId(workspaceId)) {
            throw new BadRequestException("이미 해당 워크스페이스에 프로젝트 정보가 존재합니다.");
        }

        // 아이디어 입력 정보 찾기
        IdeaInput foundIdeaInput = ideaInputRepository.findByWorkspace_WorkspaceId(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 아이디어 입력을 찾을 수 없습니다."));
        List<MainFunction> foundMainFunctions = mainFunctionRepository.findAllByIdeaInput_IdeaInputId(foundIdeaInput.getIdeaInputId());
        List<TechStack> foundTechStacks = techStackRepository.findAllByIdeaInput_IdeaInputId(foundIdeaInput.getIdeaInputId());

        List<MainFunctionData> mainFunctionDataList = foundMainFunctions.stream()
                .map(mainFunction -> new MainFunctionData(
                        mainFunction.getMainFunctionId(),
                        mainFunction.getContent()
                ))
                .collect(Collectors.toList());

        List<TechStackData> techStackDataList = foundTechStacks.stream()
                .map(techStack -> new TechStackData(
                        techStack.getTechStackId(),
                        techStack.getContent()
                ))
                .collect(Collectors.toList());

        IdeaInputRequest ideaInputRequest = new IdeaInputRequest(
                foundIdeaInput.getProjectName(),
                foundIdeaInput.getProjectTarget(),
                mainFunctionDataList,
                techStackDataList,
                foundIdeaInput.getProjectDescription()
        );

        String projectOverviewJson;
        String requirements;

        try {
            projectOverviewJson = objectMapper.writeValueAsString(ideaInputRequest);
            requirements = objectMapper.writeValueAsString(requests);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패: " + e.getMessage(), e);
        }

        // MLOps URL 설정
        String mlopsUrl = "http://3.34.185.3:8000/api/PJA/json_Summury/generate";

        ProjectInfoCreateRequest projectInfoCreateRequest = ProjectInfoCreateRequest.builder()
                .projectOverview(projectOverviewJson)
                .requirements(requirements)
                .build();

        try {
            ResponseEntity<ProjectInfoCreateResponse> response = restTemplate.postForEntity(
                    mlopsUrl,
                    projectInfoCreateRequest,
                    ProjectInfoCreateResponse.class);

            ProjectInfoCreateResponse body = response.getBody();
            ProjectInfoData projectInfoData = body.getJson().getProjectInfo();
            log.info("=== 프로젝트 정보 ml에서 받은거 : {}", projectInfoData);
            ProblemSolvingData problemSolvingData = projectInfoData.getProblemSolving();
            ProblemSolving converted = ProblemSolving.builder()
                    .currentProblem(problemSolvingData.getCurrentProblem())
                    .solutionIdea(problemSolvingData.getSolutionIdea())
                    .expectedBenefits(problemSolvingData.getExpectedBenefits())
                    .build();

            ProjectInfo savedProjectInfo = projectInfoRepository.save(
                    ProjectInfo.builder()
                            .workspace(foundWorkspace)
                            .title(projectInfoData.getTitle())
                            .category(projectInfoData.getCategory())
                            .targetUsers(projectInfoData.getTargetUsers())
                            .coreFeatures(projectInfoData.getCoreFeatures())
                            .technologyStack(projectInfoData.getTechnologyStack())
                            .problemSolving(converted)
                            .build()
            );

            return new ProjectInfoResponse(
                    savedProjectInfo.getProjectInfoId(),
                    projectInfoData.getTitle(),
                    projectInfoData.getCategory(),
                    projectInfoData.getTargetUsers(),
                    projectInfoData.getCoreFeatures(),
                    projectInfoData.getTechnologyStack(),
                    converted
            );
        }
        catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("MLOps API 호출 실패: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
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
