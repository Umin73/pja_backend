package com.project.PJA.project_progress.service;

import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.project_progress.dto.CreateCategoryAndFeatureDto;
import com.project.PJA.project_progress.entity.Feature;
import com.project.PJA.project_progress.entity.FeatureCategory;
import com.project.PJA.project_progress.entity.Progress;
import com.project.PJA.project_progress.repository.FeatureCategoryRepository;
import com.project.PJA.project_progress.repository.FeatureRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureService {

    private final WorkspaceService workspaceService;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final FeatureRepository featureRepository;
    private final FeatureCategoryRepository featureCategoryRepository;

    private static final String NO_PERMISSION = "프로젝트 진행 기능을 수정할 권한이 없습니다.";


    @Transactional
    public Long createFeature(Users user, Long workspaceId, Long categoryId, CreateCategoryAndFeatureDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 기능을 생성할 권한이 없습니다.");

        FeatureCategory category = featureCategoryRepository.findById(categoryId)
                .orElseThrow(()->new NotFoundException("기능 카테고리를 찾을 수 없습니다."));

        Set<WorkspaceMember> participants = workspaceMemberRepository.findAllById(dto.getParticipantsId())
                .stream()
                .filter(member -> member.getWorkspace().getWorkspaceId().equals(workspaceId))
                .collect(Collectors.toSet());

        Integer nextOrder = featureRepository
                .findTopByCategoryOrderByOrderIndexDesc(category)
                .map(c -> c.getOrderIndex() + 1)
                .orElse(1); // 현재 존재하는 feature 없으면 1번 부여

        Feature feature = Feature.builder()
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .state(Progress.valueOf(dto.getState().toUpperCase()))
                .importance(dto.getImportance())
                .orderIndex(nextOrder)
                .category(category)
                .participants(participants)
                .build();

        return featureRepository.save(feature).getFeatureId();
    }


    @Transactional
    public void updateName(Users user, Long workspaceId, Long featureId, String newName) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, NO_PERMISSION);

        Feature feature = getFeature(featureId);
        feature.setName(newName);
    }

    @Transactional
    public void updateStartDate(Users user, Long workspaceId, Long featureId, LocalDateTime startDate) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, NO_PERMISSION);

        Feature feature = getFeature(featureId);
        feature.setStartDate(startDate);
    }

    @Transactional
    public void updateEndDate(Users user, Long workspaceId, Long featureId, LocalDateTime endDate) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, NO_PERMISSION);

        Feature feature = getFeature(featureId);
        feature.setEndDate(endDate);
    }

    @Transactional
    public void updateState(Users user, Long workspaceId, Long featureId, Progress state) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, NO_PERMISSION);

        Feature feature = getFeature(featureId);
        feature.setState(state);
    }

    @Transactional
    public void updateImportance(Users user, Long workspaceId, Long featureId, Integer importance) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, NO_PERMISSION);

        Feature feature = getFeature(featureId);
        feature.setImportance(importance);
    }

    @Transactional
    public void updateOrderIndex(Users user, Long workspaceId, Long featureId, Integer orderIndex) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, NO_PERMISSION);

        Feature feature = getFeature(featureId);
        feature.setOrderIndex(orderIndex);
    }

    @Transactional
    public void updateParticipants(Users user, Long workspaceId, Long featureId, Set<Long> participantIds) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, NO_PERMISSION);

        Feature feature = getFeature(featureId);

        Set<WorkspaceMember> members = workspaceMemberRepository.findAllById(participantIds)
                .stream()
                .filter(member -> member.getWorkspace().getWorkspaceId().equals(workspaceId))
                .collect(Collectors.toSet());

        feature.setParticipants(members);
    }

    @Transactional
    public void deleteFeature(Users user, Long workspaceId, Long featureCategoryId, Long featureId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 기능을 삭제할 권한이 없습니다.");

        FeatureCategory featureCategory = featureCategoryRepository.findById(featureCategoryId)
                .orElseThrow(()->new NotFoundException("해당 기능 카테고리가 존재하지 않습니다."));

        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(()->new NotFoundException("해당 기능을 찾을 수 없습니다."));

        // 소속 워크스페이스 검증
        if(!feature.getCategory().getWorkspace().getWorkspaceId().equals(workspaceId)) {
            throw new IllegalArgumentException("해당 워크스페이스에 속하지 않은 기능입니다.");
        }

        // 소속 카테고리 검증
        if(!feature.getCategory().getFeatureCategoryId().equals(featureCategoryId)) {
            throw new IllegalArgumentException("해당 카테고리에 속하지 않은 기능입니다.");
        }

        featureRepository.delete(feature);
    }

    private Feature getFeature(Long id) {
        return featureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 기능을 찾을 수 없습니다."));
    }
}
