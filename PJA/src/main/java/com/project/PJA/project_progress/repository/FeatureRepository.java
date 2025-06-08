package com.project.PJA.project_progress.repository;

import com.project.PJA.project_progress.entity.Feature;
import com.project.PJA.project_progress.entity.FeatureCategory;
import com.project.PJA.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
    // Feature Category 내에 가장 order가 큰 Feature 찾아주는 쿼리
    Optional<Feature> findTopByCategoryOrderByOrderIndexDesc(FeatureCategory category);
}
