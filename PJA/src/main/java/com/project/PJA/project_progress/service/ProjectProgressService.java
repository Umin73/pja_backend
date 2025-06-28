package com.project.PJA.project_progress.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.member.service.MemberService;
import com.project.PJA.project_progress.dto.*;
import com.project.PJA.project_progress.dto.fullAiDto.*;
import com.project.PJA.project_progress.entity.*;
import com.project.PJA.project_progress.repository.ActionRepository;
import com.project.PJA.project_progress.repository.FeatureCategoryRepository;
import com.project.PJA.project_progress.repository.FeatureRepository;
import com.project.PJA.projectinfo.dto.ProjectInfoSummaryDto;
import com.project.PJA.projectinfo.entity.ProjectInfo;
import com.project.PJA.projectinfo.repository.ProjectInfoRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.enumeration.ProgressStep;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectProgressService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceService workspaceService;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectInfoRepository projectInfoRepository;
    private final FeatureCategoryRepository featureCategoryRepository;
    private final FeatureRepository featureRepository;
    private final ActionRepository actionRepository;
    private final MemberService memberService;
    private final RestTemplate restTemplate;
    private final ActionPostService actionPostService;

    @Value("${ml.path}")
    private String mlPath;

    public ProjectProgressResponseDto getProjectProcessInfo(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 비공개인데 멤버 아니면 403 반환
        workspaceService.validateWorkspaceAccess(userId, foundWorkspace);

        ProjectProgressResponseDto responseDto = new ProjectProgressResponseDto();
        log.info("ProjectProgressResponseDto 객체 생성");

        // 참여자 Set
        Set<WorkspaceMemberDto> workspaceMembers = new HashSet<>();
        responseDto.setParticipants(memberService.getMemberWithoutGuest(workspaceId));
        log.info("참여자 Set 생성");

        // 프로젝트 주요 기능 List
        List<String> coreFeatures = getCoreFeatures(foundWorkspace);
        responseDto.setCoreFeatures(coreFeatures);

        // 카테고리 리스트
        List<FeatureCategoryResponseDto> featureCategories = getFeatureCategories(foundWorkspace);
        responseDto.setFeatureCategories(featureCategories);

        log.info("최종 응답 DTO: {}", responseDto);
        return responseDto;
    }

    List<String> getCoreFeatures(Workspace workspace) {
        Optional<ProjectInfo> optionalIdea = projectInfoRepository.findByWorkspace_WorkspaceId(workspace.getWorkspaceId());
        if(optionalIdea.isEmpty()) {
            return Collections.emptyList();
        }
        ProjectInfo idea = optionalIdea.get();

        return idea.getCoreFeatures();
    }

    List<FeatureCategoryResponseDto> getFeatureCategories(Workspace workspace) {
        List<FeatureCategory> categories = Optional.ofNullable(
                featureCategoryRepository.findByWorkspaceOrderByOrderIndexAsc(workspace)
        ).orElse(new ArrayList<>());
        log.info("categories: {}",categories);

        List<FeatureCategoryResponseDto> categoryResponseDtoList = new ArrayList<>();
        for(FeatureCategory featureCategory : categories) {
            FeatureCategoryResponseDto dto = new FeatureCategoryResponseDto();

            dto.setFeatureCategoryId(featureCategory.getFeatureCategoryId());
            dto.setName(featureCategory.getName());
            dto.setOrderIndex(featureCategory.getOrderIndex());
            dto.setState(featureCategory.getState());
            dto.setHasTest(featureCategory.getHasTest());
            dto.setFeatures(getFeatures(featureCategory));

            categoryResponseDtoList.add(dto);
        }
        log.info("categories: {}",categories);
        return categoryResponseDtoList;
    }

    List<FeatureResponseDto> getFeatures(FeatureCategory category) {
        List<Feature> features = Optional.ofNullable(
                featureRepository.findByCategoryOrderByOrderIndexAsc(category)
        ).orElse(new ArrayList<>());
        log.info("features: {}",features);

        List<FeatureResponseDto> featureResponseDtoList = new ArrayList<>();
        for(Feature feature : features) {
            FeatureResponseDto dto = new FeatureResponseDto();
            dto.setFeatureId(feature.getFeatureId());
            dto.setName(feature.getName());
            dto.setOrderIndex(feature.getOrderIndex());
            dto.setState(feature.getState());
            dto.setHasTest(feature.getHasTest());
            dto.setActions(getActions(feature));

            featureResponseDtoList.add(dto);
        }
        log.info("features: {}",features);
        return featureResponseDtoList;
    }

    List<ActionResponseDto> getActions(Feature feature) {
        List<Action> actions = Optional.ofNullable(
                actionRepository.findByFeatureOrderByOrderIndexAsc(feature)
        ).orElse(new ArrayList<>());
        log.info("actions: {}",actions);

        List<ActionResponseDto> actionResponseDtoList = new ArrayList<>();
        for(Action action : actions) {
            Set<WorkspaceMember> participants = action.getParticipants().stream()
                    .map(ActionParticipant::getWorkspaceMember)
                    .collect(Collectors.toSet());

            ActionResponseDto dto = new ActionResponseDto();
            dto.setActionId(action.getActionId());
            dto.setName(action.getName());
            dto.setImportance(action.getImportance());
            dto.setParticipants(getWorkspaceMemberDto(participants));
            dto.setStartDate(action.getStartDate());
            dto.setEndDate(action.getEndDate());
            dto.setOrderIndex(action.getOrderIndex());
            dto.setState(action.getState());
            dto.setHasTest(action.getHasTest());

            if (action.getActionPost() != null) {
                dto.setActionPostId(action.getActionPost().getActionPostId());
            }

            actionResponseDtoList.add(dto);
        }
        log.info("actionResponseDtoList: {}",actionResponseDtoList);
        return actionResponseDtoList;
    }

    private Set<WorkspaceMemberDto> getWorkspaceMemberDto(Set<WorkspaceMember> participants) {
        Set<WorkspaceMemberDto> workspaceMemberDtos = new HashSet<>();
        for(WorkspaceMember member: participants) {
            WorkspaceMemberDto dto = new WorkspaceMemberDto();
            dto.setMemberId(member.getWorkspaceMemberId());
            dto.setUsername(member.getUser().getUsername());
            dto.setProfileImage(member.getUser().getProfileImage());
            dto.setRole(member.getWorkspaceRole());

            workspaceMemberDtos.add(dto);
        }
        return workspaceMemberDtos;
    }

    // 프로젝트 진행 카테고리,기능,액션 모두 AI 추천받기
    @Transactional
    public AiRecommendationResponseDto recommendFeatureStructure(Users user, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자가 오너 or 멤버가 아니면 403 반환
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "오너 또는 멤버만 AI 생성 요청을 할 수 있습니다.");

        // 프로젝트 정보 찾기
        ProjectInfo projectInfo = projectInfoRepository.findByWorkspace_WorkspaceId(workspaceId)
                .orElseThrow(() -> new NotFoundException("해당 워크스페이스에서 프로젝트 정보가 발견되지 않았습니다."));

        // AI 요청 DTO 생성
        AiProjectSummaryRequestDto requestDto = createAiRequestDto(projectInfo);

        String url = mlPath + "/api/PJA/task_generate/generate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AiProjectSummaryRequestDto> entity = new HttpEntity<>(requestDto, headers);

        try {
            // AI 요청 후 String으로 응답 받기
            ResponseEntity<String> rawResponse = restTemplate.postForEntity(url, entity, String.class);
            String responseBody = rawResponse.getBody();

            // JSON 수동 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody); // 전체 JSON 파싱
            JsonNode generatedTasksNode = root.get("generated_tasks"); // 내부 필드 접근

            AiRecommendationResponseDto parsed = mapper.treeToValue(generatedTasksNode, AiRecommendationResponseDto.class); // 원하는 DTO로 변환

            List< AiCategoryDto> aiCategories = parsed.getRecommendedCategories();
            int categoryOrder = 0;
            for (AiCategoryDto categoryDto: aiCategories) {
                // 카테고리 DB에 저장
                FeatureCategory category = FeatureCategory.builder()
                        .workspace(foundWorkspace)
                        .name(categoryDto.getName())
                        .orderIndex(categoryOrder++)
                        .hasTest(false)
                        .build();

                FeatureCategory savedCategory = featureCategoryRepository.save(category);

                int featureOrder = 0;
                for(AiFeatureDto featureDto: categoryDto.getFeatures()) {
                    // 기능 DB에 저장
                    Feature feature = Feature.builder()
                            .workspace(foundWorkspace)
                            .name(featureDto.getName())
                            .orderIndex(featureOrder++)
                            .hasTest(false)
                            .category(savedCategory)
                            .build();

                    Feature savedFeature = featureRepository.save(feature);

                    int actionOrder = 0;
                    for(AiActionDto actionDto: featureDto.getActions()) {
                        Action action = Action.builder()
                                .name(actionDto.getName())
                                .workspace(foundWorkspace)
                                .state(Progress.BEFORE)
                                .importance(actionDto.getImportance())
                                .hasTest(false)
                                .orderIndex(actionOrder++)
                                .feature(savedFeature)
                                .participants(new HashSet<>())
                                .build();

                        Action savedAction = actionRepository.save(action);

                        actionPostService.createActionPost(savedAction);
                    }
                }
            }

            foundWorkspace.updateProgressStep(ProgressStep.FIVE);

            return parsed;
        } catch (Exception e) {
            log.error("AI 추천 요청 중 예외 발생", e);
            throw new RuntimeException("AI 추천 요청 실패", e);
        }
    }

    AiProjectSummaryRequestDto createAiRequestDto(ProjectInfo info) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ProjectInfoSummaryDto dto = toSummaryDto(info);
            String jsonString = objectMapper.writeValueAsString(dto);
            return new AiProjectSummaryRequestDto(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Project JSON 변환에 실패했습니다.", e);
        }
    }

    public ProjectInfoSummaryDto toSummaryDto(ProjectInfo info) {
        return new ProjectInfoSummaryDto(
                info.getProjectInfoId(),
                info.getTitle(),
                info.getCategory(),
                info.getTargetUsers(),
                info.getCoreFeatures(),
                info.getTechnologyStack(),
                info.getProblemSolving(),
                info.getCreatedAt().toString(),
                info.getUpdatedAt().toString(),
                info.getWorkspace().getWorkspaceId()
        );
    }
}
