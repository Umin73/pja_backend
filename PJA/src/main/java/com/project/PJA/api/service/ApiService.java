package com.project.PJA.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.api.dto.*;
import com.project.PJA.api.entity.Api;
import com.project.PJA.api.repository.ApiRepository;
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
import com.project.PJA.projectinfo.dto.ProjectInfoResponse;
import com.project.PJA.projectinfo.entity.ProjectInfo;
import com.project.PJA.projectinfo.repository.ProjectInfoRepository;
import com.project.PJA.requirement.dto.RequirementResponse;
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
import org.springframework.web.client.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {
    private final WorkspaceRepository workspaceRepository;
    private final ProjectInfoRepository projectInfoRepository;
    private final RequirementRepository requirementRepository;
    private final ApiRepository apiRepository;
    private final IdeaInputRepository ideaInputRepository;
    private final MainFunctionRepository mainFunctionRepository;
    private final TechStackRepository techStackRepository;
    private final WorkspaceService workspaceService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final WorkspaceActivityService workspaceActivityService;

    // api 명세서 조회
    @Transactional(readOnly = true)
    public List<ApiResponse> getApi(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.validateWorkspaceAccess(userId, foundWorkspace);

        List<Api> apis = apiRepository.findByWorkspace_WorkspaceId(workspaceId);

        return apis.stream()
                .map(api -> new ApiResponse(
                        api.getApiId(),
                        api.getTitle(),
                        api.getTag(),
                        api.getPath(),
                        api.getHttpMethod(),
                        api.getRequest(),
                        api.getResponse()
                ))
                .collect(Collectors.toList());
    }

    // API 명세서 AI 생성 요청
    @Transactional
    public List<ApiResponse> generateApiSpecByAI(Long userId, Long workspaceId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

        if (apiRepository.existsByWorkspace_WorkspaceId(workspaceId)) {
            throw new BadRequestException("이미 해당 워크스페이스에 API 명세서가 존재합니다.");
        }

        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 아이디어 입력 찾기
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

        // 요구사항 명세서 찾기
        List<Requirement> foundRequirements = requirementRepository.findByWorkspace_WorkspaceId(workspaceId);

        List<RequirementResponse> requirementResponses = foundRequirements.stream()
                .map(req -> new RequirementResponse(
                        req.getRequirementId(),
                        req.getRequirementType(),
                        req.getContent()
                ))
                .collect(Collectors.toList());

        // 프로젝트 정보 찾기
        ProjectInfo foundProjectInfo = projectInfoRepository.findByWorkspace_WorkspaceId(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 프로젝트 정보를 찾을 수 없습니다."));

        ProjectInfoResponse projectInfoResponse = new ProjectInfoResponse(
                foundProjectInfo.getProjectInfoId(),
                foundProjectInfo.getTitle(),
                foundProjectInfo.getCategory(),
                foundProjectInfo.getTargetUsers(),
                foundProjectInfo.getCoreFeatures(),
                foundProjectInfo.getTechnologyStack(),
                foundProjectInfo.getProblemSolving()
        );

        String projectOverviewJson;
        String requirementsJson;
        String projectSummuryJson;

        try {
            projectOverviewJson = objectMapper.writeValueAsString(ideaInputRequest);
            requirementsJson = objectMapper.writeValueAsString(requirementResponses);
            projectSummuryJson = objectMapper.writeValueAsString(projectInfoResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패: " + e.getMessage(), e);
        }

        // MLOps URL 설정
        String mlopsUrl = "http://3.34.185.3:8000/api/PJA/json_API/generate";

        ApiCreateRequest apiCreateRequest = ApiCreateRequest.builder()
                .projectOverview(projectOverviewJson)
                .requirements(requirementsJson)
                .projectSummury(projectSummuryJson)
                .build();

        try {
            ResponseEntity<ApiCreateResponse> response = restTemplate.postForEntity(
                    mlopsUrl,
                    apiCreateRequest,
                    ApiCreateResponse.class
            );

            ApiCreateResponse body = response.getBody();
            log.info("body: {}", body);
            List<ApiSpecificationsData> apiSpecificationsDataList = body.getJson().getApiSpecifications();
            log.info("apiSpecificationsDataList: {}", apiSpecificationsDataList.size());

            // DB 저장
            List<Api> apiEntities = apiSpecificationsDataList.stream()
                    .map(spec -> Api.builder()
                            .workspace(foundWorkspace)
                            .title(spec.getTitle())
                            .tag(spec.getTag())
                            .path(spec.getPath())
                            .httpMethod(spec.getHttpMethod())
                            .request(spec.getRequest())
                            .response(spec.getResponse().stream()
                                    .map(res -> new ResponseData(
                                            res.getStatusCode(),
                                            res.getMessage(),
                                            res.getData()
                                    ))
                                    .collect(Collectors.toList()))
                            .build())
                    .collect(Collectors.toList());

            List<Api> savedApis = apiRepository.saveAll(apiEntities);

            return savedApis.stream()
                    .map(api -> new ApiResponse(
                            api.getApiId(),
                            api.getTitle(),
                            api.getTag(),
                            api.getPath(),
                            api.getHttpMethod(),
                            api.getRequest(),
                            api.getResponse()
                    ))
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("MLOps API 호출 실패: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.error("RestClientException 발생: {}", e.getMessage(), e);
            if (e instanceof HttpStatusCodeException ex) {
                log.error("응답 본문: {}", ex.getResponseBodyAsString());
            }
            throw new RuntimeException("MLOps API 처리 중 예외 발생", e);
        }
    }

    // api 생성
    @Transactional
    public ApiResponse createApi(Users user, Long workspaceId, ApiRequest apiRequest) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

        Api createdApi = apiRepository.save(
                Api.builder()
                        .workspace(foundWorkspace)
                        .title(apiRequest.getTitle())
                        .tag(apiRequest.getTag())
                        .path(apiRequest.getPath())
                        .httpMethod(apiRequest.getHttpMethod())
                        .request(apiRequest.getRequest())
                        .response(apiRequest.getResponse())
                        .build()
        );

        // 워크스페이스 최근 활동 데이터 추가
        workspaceActivityService.addWorkspaceActivity(user, workspaceId, ActivityTargetType.API, ActivityActionType.CREATE);

        return new ApiResponse(
                createdApi.getApiId(),
                createdApi.getTitle(),
                createdApi.getTag(),
                createdApi.getPath(),
                createdApi.getHttpMethod(),
                createdApi.getRequest(),
                createdApi.getResponse()
        );
    }

    // api 수정
    @Transactional
    public ApiResponse updateApi(Users user, Long workspaceId, Long apiId, ApiRequest apiRequest) {
        Api foundApi = apiRepository.findById(apiId)
                .orElseThrow(() -> new NotFoundException("요청하신 API를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "이 워크스페이스에 수정할 권한이 없습니다.");

        foundApi.update(
                apiRequest.getTitle(),
                apiRequest.getTag(),
                apiRequest.getPath(),
                apiRequest.getHttpMethod(),
                apiRequest.getRequest(),
                apiRequest.getResponse());

        // 워크스페이스 최근 활동 데이터 추가
        workspaceActivityService.addWorkspaceActivity(user, workspaceId, ActivityTargetType.API, ActivityActionType.UPDATE);


        return new ApiResponse(
                foundApi.getApiId(),
                apiRequest.getTitle(),
                apiRequest.getTag(),
                apiRequest.getPath(),
                apiRequest.getHttpMethod(),
                apiRequest.getRequest(),
                apiRequest.getResponse()
        );
    }

    // api 삭제
    @Transactional
    public ApiResponse deleteApi(Users user, Long workspaceId, Long apiId) {
        Api foundApi = apiRepository.findById(apiId)
                .orElseThrow(() -> new NotFoundException("요청하신 API를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "이 워크스페이스에 삭제할 권한이 없습니다.");

        apiRepository.delete(foundApi);

        // 워크스페이스 최근 활동 데이터 추가
        workspaceActivityService.addWorkspaceActivity(user, workspaceId, ActivityTargetType.API, ActivityActionType.UPDATE);


        return new ApiResponse(
                foundApi.getApiId(),
                foundApi.getTitle(),
                foundApi.getTag(),
                foundApi.getPath(),
                foundApi.getHttpMethod(),
                foundApi.getRequest(),
                foundApi.getResponse()
        );
    }
}
