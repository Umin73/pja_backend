package com.project.PJA.project_progress.service;

import com.project.PJA.exception.NotFoundException;
import com.project.PJA.projectinfo.entity.ProjectInfo;
import com.project.PJA.projectinfo.repository.ProjectInfoRepository;
import com.project.PJA.member.service.MemberService;
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
    private final ProjectInfoRepository projectInfoRepository;
    private final FeatureCategoryRepository featureCategoryRepository;
    private final FeatureRepository featureRepository;
    private final ActionRepository actionRepository;
    private final MemberService memberService;

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
            throw new NotFoundException("아이디어가 존재하지 않습니다.");
        }
        ProjectInfo idea = optionalIdea.get();

        return idea.getCoreFeatures();
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
            dto.setParticipants(getWorkspaceMemberDto(action.getParticipants()));
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
}
