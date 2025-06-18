package com.project.PJA.project_progress.service;

import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.project_progress.dto.CreateActionDto;
import com.project.PJA.project_progress.dto.CreateCategoryAndFeatureDto;
import com.project.PJA.project_progress.dto.UpdateActionDto;
import com.project.PJA.project_progress.dto.UpdateFeatureAndCategoryDto;
import com.project.PJA.project_progress.entity.Feature;
import com.project.PJA.project_progress.entity.FeatureCategory;
import com.project.PJA.project_progress.entity.Progress;
import com.project.PJA.project_progress.repository.FeatureCategoryRepository;
import com.project.PJA.project_progress.repository.FeatureRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureService {

    private final WorkspaceService workspaceService;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final FeatureRepository featureRepository;
    private final FeatureCategoryRepository featureCategoryRepository;

    @Transactional
    public Long createFeature(Users user, Long workspaceId, Long categoryId, CreateCategoryAndFeatureDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 기능을 생성할 권한이 없습니다.");

        FeatureCategory category = featureCategoryRepository.findById(categoryId)
                .orElseThrow(()->new NotFoundException("기능 카테고리를 찾을 수 없습니다."));

        Integer nextOrder = featureRepository
                .findTopByCategoryOrderByOrderIndexDesc(category)
                .map(c -> c.getOrderIndex() + 1)
                .orElse(1); // 현재 존재하는 feature 없으면 1번 부여

        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("워크스페이스가 존재하지 않습니다."));

        Feature feature = Feature.builder()
                .name(dto.getName())
                .workspace(foundWorkspace)
                .state(dto.getState())
                .hasTest(false)
                .orderIndex(nextOrder)
                .category(category)
                .build();

        return featureRepository.save(feature).getFeatureId();
    }

    @Transactional
    public void updateFeature(Users user, Long workspaceId, Long categoryId, Long featureId, UpdateFeatureAndCategoryDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 기능을 수정할 권한이 없습니다.");

        Feature feature = getFeature(featureId);
        validateFeatureHierarchy(workspaceId, categoryId, featureId, feature);

        if (dto.getName() != null) feature.setName(dto.getName());
        if (dto.getState() != null) feature.setState(dto.getState());
        if (dto.getOrderIndex() != null) feature.setOrderIndex(dto.getOrderIndex());
        if (dto.getHasTest() != null) feature.setHasTest(dto.getHasTest());
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

    private void validateFeatureHierarchy(Long workspaceId, Long categoryId, Long featureId, Feature feature) {
        FeatureCategory category = feature.getCategory();

        if (!category.getFeatureCategoryId().equals(categoryId)) {
            throw new ForbiddenException("기능이 지정된 카테고리에 속하지 않습니다.");
        }

        if (!category.getWorkspace().getWorkspaceId().equals(workspaceId)) {
            throw new ForbiddenException("카테고리가 지정된 워크스페이스에 속하지 않습니다.");
        }
    }

    private Feature getFeature(Long id) {
        return featureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 기능을 찾을 수 없습니다."));
    }
}
