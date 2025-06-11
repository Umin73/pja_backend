package com.project.PJA.project_progress.service;

import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.project_progress.dto.CreateProgressDto;
import com.project.PJA.project_progress.dto.UpdateProgressDto;
import com.project.PJA.project_progress.entity.Action;
import com.project.PJA.project_progress.entity.Feature;
import com.project.PJA.project_progress.entity.FeatureCategory;
import com.project.PJA.project_progress.entity.Progress;
import com.project.PJA.project_progress.repository.FeatureCategoryRepository;
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
public class FeatureCategoryService {

    private final WorkspaceService workspaceService;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final FeatureCategoryRepository featureCategoryRepository;
    private final WorkspaceRepository workspaceRepository;

    @Transactional
    public Long createFeatureCategory(Users user, Long workspaceId, CreateProgressDto dto) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(()->new NotFoundException("해당 워크스페이스가 존재하지 않습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 카테고리를 생성할 권한이 없습니다.");

        Integer nextOrder = featureCategoryRepository
                .findTopByWorkspaceOrderByOrderIndexDesc(workspace)
                .map(c -> c.getOrderIndex() + 1)
                .orElse(1); // 현재 존재하는 feature category 없으면 1번 부여

        FeatureCategory featureCategory = FeatureCategory.builder()
                .name(dto.getName())
                .state(Progress.valueOf(dto.getState().toUpperCase()))
                .hasTest(false)
                .orderIndex(nextOrder)
                .workspace(workspace)
                .build();
        return featureCategoryRepository.save(featureCategory).getFeatureCategoryId();
    }

    @Transactional
    public void updateCategory(Users user, Long workspaceId, Long categoryId, UpdateProgressDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 카테고리를 수정할 권한이 없습니다.");

        FeatureCategory category = getCategory(categoryId);

        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getState() != null) category.setState(Progress.valueOf(dto.getState().toUpperCase()));
        if (dto.getOrderIndex() != null) category.setOrderIndex(dto.getOrderIndex());
        if (dto.getHasTest() != null) category.setHasTest(dto.getHasTest());
    }

    @Transactional
    public void deleteFeatureCategory(Users user, Long workspaceId, Long categoryId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(()->new NotFoundException("해당 워크스페이스가 존재하지 않습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 카테고리를 삭제할 권한이 없습니다.");

        FeatureCategory category = getCategory(categoryId);

        // 소속 워크스페이스 검증
        if(!category.getWorkspace().getWorkspaceId().equals(workspaceId)) {
            throw new ForbiddenException("해당 워크스페이스에 속하지 않은 카테고리입니다.");
        }

        // Feature, Action도 함께 삭제됨
        featureCategoryRepository.delete(category);
    }

    private FeatureCategory getCategory(Long id) {
        return featureCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 기능 카테고리를 찾을 수 없습니다."));
    }
}
