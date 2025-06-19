package com.project.PJA.requirement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.project.PJA.requirement.dto.*;
import com.project.PJA.requirement.entity.Requirement;
import com.project.PJA.requirement.repository.RequirementRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import com.project.PJA.workspace_activity.enumeration.ActivityActionType;
import com.project.PJA.workspace_activity.enumeration.ActivityTargetType;
import com.project.PJA.workspace_activity.service.WorkspaceActivityService;
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
public class RequirementService {
    private final RequirementRepository requirementRepository;
    private final WorkspaceRepository workspaceRepository;
    private final IdeaInputRepository ideaInputRepository;
    private final MainFunctionRepository mainFunctionRepository;
    private final TechStackRepository techStackRepository;
    private final WorkspaceService workspaceService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WorkspaceActivityService workspaceActivityService;

    // 요구사항 명세서 조회
    @Transactional(readOnly = true)
    public List<RequirementResponse> getRequirement(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 비공개인데 멤버 아니면 403 반환
        workspaceService.validateWorkspaceAccess(userId, foundWorkspace);

        // 보여주기
        List<Requirement> requirements = requirementRepository.findByWorkspace_WorkspaceId(workspaceId);

        return requirements.stream()
                .map(req -> new RequirementResponse(
                        req.getRequirementId(),
                        req.getRequirementType(),
                        req.getContent()
                ))
                .collect(Collectors.toList());
    }

    // 요구사항 명세서 AI 생성 요청
    public List<RequirementRequest> recommendRequirement(Long userId, Long workspaceId, List<RequirementRequest> requests) {
        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

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
        String existingRequirementsJson;

        try {
            projectOverviewJson = objectMapper.writeValueAsString(ideaInputRequest);
            existingRequirementsJson = objectMapper.writeValueAsString(requests);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패: " + e.getMessage(), e);
        }

        String mlopsUrl = "http://3.34.185.3:8000/api/PJA/requirements/generate";

        RequirementRecommendationRequest recommendationRequest = RequirementRecommendationRequest.builder()
                .projectOverview(projectOverviewJson)
                .existingRequirements(existingRequirementsJson)
                .build();

        try {
            ResponseEntity<RequirementRecommendationResponse> response = restTemplate.postForEntity(
                    mlopsUrl,
                    recommendationRequest,
                    RequirementRecommendationResponse.class);

            RequirementRecommendationResponse body = response.getBody();

            return body.getRequirements();
        }
        catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("MLOps API 호출 실패: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    // 요구사항 명세서 생성
    @Transactional
    public RequirementResponse createRequirement(Users user, Long workspaceId, RequirementRequest requirementRequest) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

        Requirement createdRequirement = requirementRepository.save(
                Requirement.builder()
                        .workspace(foundWorkspace)
                        .requirementType(requirementRequest.getRequirementType())
                        .content(requirementRequest.getContent())
                        .build()
        );

        // 최근 활동 기록 추가
        workspaceActivityService.addWorkspaceActivity(user, workspaceId, ActivityTargetType.REQUIREMENT, ActivityActionType.CREATE);

        return new RequirementResponse(
                createdRequirement.getRequirementId(),
                createdRequirement.getRequirementType(),
                createdRequirement.getContent()
        );
    }

    // 요구사항 명세서 수정
    @Transactional
    public RequirementResponse updateRequirement(Users user, Long workspaceId, Long requirementId, RequirementContentRequest requirementContentRequest) {
        Requirement foundRequirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new NotFoundException("요청하신 요구사항을 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "이 워크스페이스에 수정할 권한이 없습니다.");

        foundRequirement.update(requirementContentRequest.getContent());

        // 최근 활동 기록 추가
        workspaceActivityService.addWorkspaceActivity(user, workspaceId, ActivityTargetType.REQUIREMENT, ActivityActionType.UPDATE);

        return new RequirementResponse(
                foundRequirement.getRequirementId(),
                foundRequirement.getRequirementType(),
                requirementContentRequest.getContent()
        );
    }

    // 요구사항 명세서 삭제
    @Transactional
    public RequirementResponse deleteRequirement(Users user, Long workspaceId, Long requirementId) {
        Requirement foundRequirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new NotFoundException("요청하신 요구사항을 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "이 워크스페이스에 삭제할 권한이 없습니다.");

        requirementRepository.delete(foundRequirement);

        // 최근 활동 기록 추가
        workspaceActivityService.addWorkspaceActivity(user, workspaceId, ActivityTargetType.REQUIREMENT, ActivityActionType.DELETE);

        return new RequirementResponse(
                foundRequirement.getRequirementId(),
                foundRequirement.getRequirementType(),
                foundRequirement.getContent()
        );
    }
}
