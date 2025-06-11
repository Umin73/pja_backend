package com.project.PJA.project_progress.repository;

import com.project.PJA.project_progress.entity.Action;
import com.project.PJA.project_progress.entity.Feature;
import com.project.PJA.project_progress.entity.FeatureCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActionRepository extends JpaRepository<Action, Long> {
    // Feature 내에 가장 order가 큰 Action 찾아주는 쿼리
    Optional<Action> findTopByFeatureOrderByOrderIndexDesc(Feature feature);
    // Feature로 Action 리스트 찾기
    List<Action> findActionsByFeature(Feature feature);
}
