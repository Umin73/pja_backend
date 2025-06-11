package com.project.PJA.project_progress.service;

import com.project.PJA.exception.NotFoundException;
import com.project.PJA.idea.repository.IdeaRepository;
import com.project.PJA.project_progress.dto.*;
import com.project.PJA.project_progress.entity.Action;
import com.project.PJA.project_progress.entity.Feature;
import com.project.PJA.project_progress.entity.FeatureCategory;
import com.project.PJA.project_progress.repository.ActionRepository;
import com.project.PJA.project_progress.repository.FeatureCategoryRepository;
import com.project.PJA.project_progress.repository.FeatureRepository;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectProgressService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceService workspaceService;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final IdeaRepository ideaRepository;
    private final FeatureCategoryRepository featureCategoryRepository;
    private final FeatureRepository featureRepository;
    private final ActionRepository actionRepository;

    public ProjectProgressResponseDto getProjectProcessInfo(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 비공개인데 멤버 아니면 403 반환
        workspaceService.validateWorkspaceAccess(userId, foundWorkspace);

        ProjectProgressResponseDto responseDto = new ProjectProgressResponseDto();
        log.info("ProjectProgressResponseDto 객체 생성");

        // 참여자 Set
        Set<WorkspaceMemberDto> workspaceMembers = new HashSet<>();
        responseDto.setParticipants(Collections.emptySet());
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


//    List<WorkspaceMember> getWorkspaceMembers(Long workspaceId) {
//        Set<WorkspaceMember> members = workspaceMemberRepository.findAllById(dto.getParticipantIds()).stream()
//                .filter(member -> member.getWorkspace().getWorkspaceId().equals(workspaceId))
//                .collect(Collectors.toSet());
//    }

    List<String> getCoreFeatures(Workspace workspace) {
//        Optional<Idea> optionalIdea = ideaRepository.findByWorkspace_WorkspaceId(workspace.getWorkspaceId());
//        if(optionalIdea.isEmpty()) {
//            throw new NotFoundException("아이디어가 존재하지 않습니다.");
//        }
//        Idea idea = optionalIdea.get();
//
//        return idea.getCoreFeatures();
        return new ArrayList<>(); // 일단 임시로 빈 array list 반환
    }

    List<FeatureCategoryResponseDto> getFeatureCategories(Workspace workspace) {
        List<FeatureCategory> categories = Optional.ofNullable(
                featureCategoryRepository.findFeatureCategoriesByWorkspace(workspace)
        ).orElse(new ArrayList<>());
        log.info("categories: {}",categories);

        List<FeatureCategoryResponseDto> categoryResponseDtoList = new ArrayList<>();
        for(FeatureCategory featureCategory : categories) {
            FeatureCategoryResponseDto dto = new FeatureCategoryResponseDto();

            dto.setFeatureCategoryId(featureCategory.getFeatureCategoryId());
            dto.setName(featureCategory.getName());
            dto.setImportance(featureCategory.getImportance());
            dto.setParticipants(Collections.emptySet());
            dto.setStartDate(featureCategory.getStartDate());
            dto.setEndDate(featureCategory.getEndDate());
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
                featureRepository.findFeaturesByCategory(category)
        ).orElse(new ArrayList<>());
        log.info("features: {}",features);

        List<FeatureResponseDto> featureResponseDtoList = new ArrayList<>();
        for(Feature feature : features) {
            FeatureResponseDto dto = new FeatureResponseDto();
            dto.setFeatureId(feature.getFeatureId());
            dto.setName(feature.getName());
            dto.setImportance(feature.getImportance());
            dto.setParticipants(Collections.emptySet());
            dto.setStartDate(feature.getStartDate());
            dto.setEndDate(feature.getEndDate());
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
                actionRepository.findActionsByFeature(feature)
        ).orElse(new ArrayList<>());
        log.info("actions: {}",actions);

        List<ActionResponseDto> actionResponseDtoList = new ArrayList<>();
        for(Action action : actions) {
            ActionResponseDto dto = new ActionResponseDto();
            dto.setActionId(action.getActionId());
            dto.setName(action.getName());
            dto.setImportance(action.getImportance());
            dto.setParticipants(Collections.emptySet());
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
}
