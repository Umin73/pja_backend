package com.project.PJA.project_progress.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.PJA.common.user_act_log.UserActionLogService;
import com.project.PJA.common.user_act_log.UserActionType;
import com.project.PJA.exception.BadRequestException;
import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.ideainput.dto.IdeaInputRequest;
import com.project.PJA.ideainput.dto.MainFunctionData;
import com.project.PJA.ideainput.dto.TechStackData;
import com.project.PJA.ideainput.entity.IdeaInput;
import com.project.PJA.ideainput.entity.MainFunction;
import com.project.PJA.ideainput.entity.TechStack;
import com.project.PJA.project_progress.dto.CreateProgressDto;
import com.project.PJA.project_progress.dto.UpdateProgressDto;
import com.project.PJA.project_progress.dto.aiDto.*;
import com.project.PJA.project_progress.entity.Action;
import com.project.PJA.project_progress.entity.Feature;
import com.project.PJA.project_progress.entity.FeatureCategory;
import com.project.PJA.project_progress.entity.Progress;
import com.project.PJA.project_progress.repository.ActionRepository;
import com.project.PJA.project_progress.repository.FeatureRepository;
import com.project.PJA.projectinfo.dto.*;
import com.project.PJA.projectinfo.entity.ProjectInfo;
import com.project.PJA.requirement.dto.RequirementRequest;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionService {

    private final WorkspaceService workspaceService;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final FeatureRepository featureRepository;
    private final ActionRepository actionRepository;
    private final ActionPostService actionPostService;
    private final UserActionLogService userActionLogService;
    private final RestTemplate restTemplate;

    @Transactional
    public Long createAction(Users user, Long workspaceId, Long categoryId, Long featureId, CreateProgressDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션을 생성할 권한이 없습니다.");

        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(()->new NotFoundException("기능을 찾을 수 없습니다."));

        validateFeatureHierarchy(workspaceId, categoryId, featureId, feature);


        Set<WorkspaceMember> participants = workspaceMemberRepository.findAllById(dto.getParticipantsId())
                .stream()
                .filter(member -> member.getWorkspace().getWorkspaceId().equals(workspaceId))
                .collect(Collectors.toSet());

        Integer nextOrder = actionRepository
                .findTopByFeatureOrderByOrderIndexDesc(feature)
                .map(c -> c.getOrderIndex() + 1)
                .orElse(1); // 현재 존재하는 action 없으면 1번 부여

        Action action = Action.builder()
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .state(Progress.valueOf(dto.getState().toUpperCase()))
                .importance(dto.getImportance())
                .hasTest(false)
                .orderIndex(nextOrder)
                .feature(feature)
                .participants(participants)
                .build();

        actionRepository.save(action);

        actionPostService.createActionPost(action);

        // 액션 생성 시 -> 유저 행동 로그 데이터 남김
        userActionLogService.log(
                UserActionType.CREATE_PROJECT_PROGRESS_ACTION,
                String.valueOf(user.getUserId()),
                user.getUsername(),
                workspaceId,
                Map.of(
                        "name", action.getName(),
                        "state", action.getState().name(),
                        "importance", action.getImportance(),
                        "participants", action.getParticipants().stream()
                                .map(pm -> Map.of(
                                        "userId", pm.getUser().getUserId(),
                                        "username", pm.getUser().getUsername()
                                ))
                                .collect(Collectors.toList())
                )
        );

        return action.getActionId();
    }

    // 프로젝트 액션 AI 추천
    @Transactional(readOnly = true)
    public List<RecommendedAction> recommendedActions(Users user, Long workspaceId, Long actionId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션을 추천받을 권한이 없습니다.");

        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException("해당 액션을 찾을 수 없습니다."));

        Feature feature = action.getFeature();
        FeatureCategory category = feature.getCategory();
        List<Action> actionList = actionRepository.findActionsByFeature(feature);

        List<AiRequestAction> aiRequestActionList = actionList.stream()
                .map(ac -> new AiRequestAction(
                        ac.getActionId(),
                        ac.getName(),
                        ac.getStartDate(),
                        ac.getEndDate(),
                        ac.getImportance()
                ))
                .toList();

        AiRequestFeature aiRequestFeature = new AiRequestFeature(
                feature.getFeatureId(),
                feature.getName(),
                aiRequestActionList
        );

        AiRequestCategory aiRequestCategory = new AiRequestCategory(
                category.getFeatureCategoryId(),
                category.getName(),
                aiRequestFeature
        );

        String mlopsUrl = "http://3.34.185.3:8000/api/PJA/recommend/generate";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("project_list", aiRequestCategory); // 실제 JSON 구조에 맞게 조정
        requestBody.put("max_tokens", 3000);
        requestBody.put("temperature", 0.3);
        requestBody.put("model", "gpt-4o-mini");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<RecommendationResponse> response = restTemplate.exchange(
                    mlopsUrl,
                    HttpMethod.POST,
                    request,
                    RecommendationResponse.class
            );

            RecommendationResponse body = response.getBody();
            List<RecommendedAction> result = body.getRecommendations().getRecommendedActions();

            log.info("추천된 액션 리스트: {}", result);
            return result;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("MLOps API 호출 실패: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    @Transactional
    public void updateAction(Users user, Long workspaceId, Long categoryId, Long featureId, Long actionId, UpdateProgressDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션을 수정할 권한이 없습니다.");

        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException("액션이 존재하지 않습니다."));

        Feature feature = action.getFeature();
        validateFeatureHierarchy(workspaceId, categoryId, featureId, feature);

        if (dto.getName() != null) action.setName(dto.getName());
        if (dto.getStartDate() != null) action.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) action.setEndDate(dto.getEndDate());
        if (dto.getState() != null) {
            action.setState(Progress.valueOf(dto.getState().toUpperCase()));

            if(Progress.valueOf(dto.getState().toUpperCase()).equals(Progress.DONE)) {
                // 상태가 완료로 변경될 시 -> 유저 행동 로그 데이터 남김
                userActionLogService.log(
                        UserActionType.DONE_PROJECT_PROGRESS_ACTION,
                        String.valueOf(user.getUserId()),
                        user.getUsername(),
                        workspaceId,
                        Map.of(
                                "name", action.getName(),
                                "state", action.getState().name(),
                                "importance", action.getImportance(),
                                "startDate", action.getStartDate(),
                                "endDate", LocalDateTime.now(),
                                "participants", action.getParticipants().stream()
                                        .map(pm -> Map.of(
                                                "userId", pm.getUser().getUserId(),
                                                "username", pm.getUser().getUsername()
                                        ))
                                        .collect(Collectors.toList())
                        )
                );
            }
        }
        if (dto.getImportance() != null) action.setImportance(dto.getImportance());
        if (dto.getOrderIndex() != null) action.setOrderIndex(dto.getOrderIndex());
        if (dto.getHasTest() != null) action.setHasTest(dto.getHasTest());
        if (dto.getParticipantIds() != null) {
            Set<WorkspaceMember> members = workspaceMemberRepository.findAllById(dto.getParticipantIds()).stream()
                    .filter(member -> member.getWorkspace().getWorkspaceId().equals(workspaceId))
                    .collect(Collectors.toSet());
            action.setParticipants(members);

            // 참여자 추가(수정) 시 -> 유저 행동 로그 데이터 남김
            userActionLogService.log(
                    UserActionType.UPDATE_PARTICIPANT_TO_ACTION,
                    String.valueOf(user.getUserId()),
                    user.getUsername(),
                    workspaceId,
                    Map.of(
                            "name", action.getName(),
                            "state", action.getState().name(),
                            "importance", action.getImportance(),
                            "participants", action.getParticipants().stream()
                                    .map(pm -> Map.of(
                                            "userId", pm.getUser().getUserId(),
                                            "username", pm.getUser().getUsername()
                                    ))
                                    .collect(Collectors.toList())
                    )
            );
        }
    }

    @Transactional
    public void deleteAction(Users user, Long workspaceId, Long categoryId, Long featureId, Long actionId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션을 삭제할 권한이 없습니다.");

        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException("액션이 존재하지 않습니다."));

        Feature feature = action.getFeature();
        validateFeatureHierarchy(workspaceId, categoryId, featureId, feature);

        actionRepository.delete(action);

        // 액션 삭제 시 -> 유저 행동 로그 데이터 남김
        userActionLogService.log(
                UserActionType.DELETE_PROJECT_PROGRESS_ACTION,
                String.valueOf(user.getUserId()),
                user.getUsername(),
                workspaceId,
                Map.of(
                        "name", action.getName(),
                        "state", action.getState().name(),
                        "importance", action.getImportance(),
                        "participants", action.getParticipants().stream()
                                .map(pm -> Map.of(
                                        "userId", pm.getUser().getUserId(),
                                        "username", pm.getUser().getUsername()
                                ))
                                .collect(Collectors.toList())
                )
        );
    }

    private void validateFeatureHierarchy(Long workspaceId, Long categoryId, Long featureId, Feature feature) {
        FeatureCategory category = feature.getCategory();

        if (!category.getFeatureCategoryId().equals(categoryId)) {
            throw new ForbiddenException("기능이 지정된 카테고리에 속하지 않습니다.");
        }

        if (!category.getWorkspace().getWorkspaceId().equals(workspaceId)) {
            throw new ForbiddenException("카테고리가 지정된 워크스페이스에 속하지 않습니다.");
        }
    }

}
