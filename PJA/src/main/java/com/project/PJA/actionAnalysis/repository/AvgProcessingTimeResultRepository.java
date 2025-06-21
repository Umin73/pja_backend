package com.project.PJA.actionAnalysis.repository;

import com.project.PJA.actionAnalysis.entity.AvgProcessingTimeResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvgProcessingTimeResultRepository extends JpaRepository<AvgProcessingTimeResult, Long> {
    void deleteByWorkspaceIdAndUserIdAndImportance(Long workspaceId, Long userId, Integer importance);
    List<AvgProcessingTimeResult> findByWorkspaceId(Long workspaceId);
}
