package com.project.PJA.project_progress.repository;

import com.project.PJA.project_progress.entity.FeatureCategory;
import com.project.PJA.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureCategoryRepository extends JpaRepository<FeatureCategory, Long> {

    // 워크스페이스 내에 가장 order가 큰 FeatrueCategory를 찾아주는 쿼리
    Optional<FeatureCategory> findTopByWorkspaceOrderByOrderIndexDesc(Workspace workspace);
}
