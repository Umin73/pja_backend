package com.project.PJA.project_progress.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.project_progress.dto.MyActionDto;
import com.project.PJA.user_act_log.service.UserActionLogService;
import com.project.PJA.user_act_log.enumeration.UserActionType;
import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.project_progress.dto.CreateActionDto;
import com.project.PJA.project_progress.dto.OnlyActionResponseDto;
import com.project.PJA.project_progress.dto.UpdateActionDto;
import com.project.PJA.project_progress.dto.aiDto.*;
import com.project.PJA.project_progress.entity.*;
import com.project.PJA.project_progress.repository.ActionRepository;
import com.project.PJA.project_progress.repository.FeatureRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import com.project.PJA.workspace_activity.enumeration.ActivityActionType;
import com.project.PJA.workspace_activity.enumeration.ActivityTargetType;
import com.project.PJA.workspace_activity.service.WorkspaceActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final ObjectMapper objectMapper;
    private final WorkspaceActivityService workspaceActivityService;

    @Transactional(readOnly = true)
    public List<OnlyActionResponseDto> readActionList(Users user, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                        .orElseThrow(() -> new NotFoundException("워크스페이스가 존재하지 않습니다."));

        workspaceService.validateWorkspaceAccess(user.getUserId(), foundWorkspace);

        List<Action> actionList = actionRepository.findByFeature_Workspace_WorkspaceId(workspaceId);

        List<OnlyActionResponseDto> dtoList = new ArrayList<>();
        for(Action action : actionList) {
            OnlyActionResponseDto dto = new OnlyActionResponseDto();
            dto.setActionId(action.getActionId());
            dto.setActionName(action.getName());
            dto.setStartDate(action.getStartDate());
            dto.setEndDate(action.getEndDate());
            dto.setActionPostId(action.getActionPost().getActionPostId());

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Transactional(readOnly = true)
    public List<MyActionDto> readMyToDoActionList(Users user, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("워크스페이스 아이디로 워크스페이스를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "해당 워크스페이스의 오너 또는 멤버가 아니면 내 작업을 확인할 수 없습니다.");

        List<Action> actionList = actionRepository.findAllByWorkspaceIdAndUserIdAndStateIsBeforeOrInProgress(workspaceId, user.getUserId());
        List<MyActionDto> dtoList = new ArrayList<>();
        for(Action action : actionList) {
            MyActionDto dto = new MyActionDto();
            dto.setActionId(action.getActionId());
            dto.setActionName(action.getName());
            dto.setState(action.getState().toString());
            dto.setEndDate(action.getEndDate());

            dtoList.add(dto);
        }

        return dtoList;
    }

    @Transactional
    public Long createAction(Users user, Long workspaceId, Long categoryId, Long featureId, CreateActionDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션을 생성할 권한이 없습니다.");

        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(()->new NotFoundException("기능을 찾을 수 없습니다."));

        validateFeatureHierarchy(workspaceId, categoryId, featureId, feature);


//        Set<WorkspaceMember> participants = workspaceMemberRepository.findAllById(dto.getParticipantsId())
//                .stream()
//                .filter(member -> member.getWorkspace().getWorkspaceId().equals(workspaceId))
//                .collect(Collectors.toSet());

        Integer nextOrder = actionRepository
                .findTopByFeatureOrderByOrderIndexDesc(feature)
                .map(c -> c.getOrderIndex() + 1)
                .orElse(1); // 현재 존재하는 action 없으면 1번 부여

        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(()-> new NotFoundException("워크스페이스가 존재하지 않습니다."));

        Action action = Action.builder()
                .name(dto.getName())
                .workspace(foundWorkspace)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .state(Progress.valueOf(dto.getState().toUpperCase()))
                .importance(dto.getImportance())
                .hasTest(false)
                .orderIndex(nextOrder)
                .feature(feature)
                .participants(new HashSet<>())
                .build();

        actionRepository.save(action);

        Set<ActionParticipant> actionParticipants
                = workspaceMemberRepository.findAllById(dto.getParticipantsId()).stream()
                        .filter(m -> m.getWorkspace().getWorkspaceId().equals(workspaceId))
                                .map(member -> ActionParticipant.builder()
                                        .action(action)
                                        .workspaceMember(member)
                                        .build())
                                        .collect(Collectors.toSet());
        action.setParticipants(actionParticipants);

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
                                .map(p -> Map.of(
                                        "userId", p.getWorkspaceMember().getUser().getUserId(),
                                        "username", p.getWorkspaceMember().getUser().getUsername()
                                ))
                                .collect(Collectors.toList())
                )
        );

        // 최근 활동 기록 추가
        workspaceActivityService.addWorkspaceActivity(user, workspaceId, ActivityTargetType.ACTION, ActivityActionType.CREATE);

        return action.getActionId();
    }

    // 프로젝트 액션 AI 추천
    @Transactional(readOnly = true)
    public ActionRecommendationJson recommendedActions(Users user, Long workspaceId, Long featureId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션을 추천받을 권한이 없습니다.");

        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new NotFoundException("해당 기능을 찾을 수 없습니다."));

        FeatureCategory category = feature.getCategory();
        List<Action> actionList = actionRepository.findActionsByFeature(feature);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<ActionData> actionDataList = actionList.stream().map(
                ac -> new ActionData(
                        ac.getName(),
                        ac.getImportance(),
                        ac.getStartDate().format(formatter),
                        ac.getEndDate().format(formatter)
                )
        ).toList();

        FeatureData featureData = new FeatureData(
                feature.getFeatureId(),
                feature.getName(),
                actionDataList
        );

        CategoryData categoryData = new CategoryData(
                category.getFeatureCategoryId(),
                category.getName(),
                featureData
        );

        // 직렬화용 객체로 변환
        String projectDataJson;

        try {
            projectDataJson = objectMapper.writeValueAsString(categoryData);
        } catch (Exception e) {
            throw new RuntimeException("JSON 직렬화 실패: " + e.getMessage(), e);
        }

        ActionAiRequestDto aiRequestDto = ActionAiRequestDto.builder()
                .project_list(projectDataJson)
                .max_tokens(3000L)
                .temperature(0.3)
                .model("gpt-4o")
                .build();

        String mlopsUrl = "http://3.34.185.3:8000/api/PJA/recommend/generate";

        try {
            ResponseEntity<ActionAiRecommendedResponse> response = restTemplate.postForEntity(
                    mlopsUrl,
                    aiRequestDto,
                    ActionAiRecommendedResponse.class
            );

            ActionAiRecommendedResponse body = response.getBody();

            if (body == null || body.getJson() == null) {
                throw new RuntimeException("추천 결과가 비어 있습니다.");
            }

            log.info("body: {}", body.toString());

            return body.getJson();

        }  catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("MLOps API 호출 실패: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    @Transactional
    public Map<String, Object> updateAction(Users user, Long workspaceId, Long categoryId, Long featureId, Long actionId, UpdateActionDto dto) {
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
                                                "userId", pm.getWorkspaceMember().getUser().getUserId(),
                                                "username", pm.getWorkspaceMember().getUser().getUsername()
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
            action.getParticipants().clear(); // 기존 ActionParticipant 제거

            Set<ActionParticipant> updatedParticipants
                    = workspaceMemberRepository.findAllById(dto.getParticipantIds())
                    .stream().map(
                            member -> ActionParticipant.builder()
                                    .action(action)
                                    .workspaceMember(member)
                                    .build()
                    ).collect(Collectors.toSet());

            action.getParticipants().addAll(updatedParticipants);

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
                                            "userId", pm.getWorkspaceMember().getUser().getUserId(),
                                            "username", pm.getWorkspaceMember().getUser().getUsername()
                                    ))
                                    .collect(Collectors.toList())
                    )
            );
        }

        // 최근 활동 기록 추가
        workspaceActivityService.addWorkspaceActivity(user, workspaceId, ActivityTargetType.ACTION, ActivityActionType.UPDATE);

        return Map.of("actionId", actionId, "actionPostId", action.getActionPost().getActionPostId());
    }

    @Transactional
    public void deleteAction(Users user, Long workspaceId, Long categoryId, Long featureId, Long actionId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션을 삭제할 권한이 없습니다.");

        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new NotFoundException("액션이 존재하지 않습니다."));

        Feature feature = action.getFeature();
        validateFeatureHierarchy(workspaceId, categoryId, featureId, feature);

        actionRepository.delete(action);

        // 최근 활동 기록 추가
        workspaceActivityService.addWorkspaceActivity(user, workspaceId, ActivityTargetType.ACTION, ActivityActionType.DELETE);

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
                                        "userId", pm.getWorkspaceMember().getUser().getUserId(),
                                        "username", pm.getWorkspaceMember().getUser().getUsername()
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
