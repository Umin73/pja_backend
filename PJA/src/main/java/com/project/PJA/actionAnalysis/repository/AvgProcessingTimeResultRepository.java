package com.project.PJA.actionAnalysis.repository;

import com.project.PJA.actionAnalysis.entity.AvgProcessingTimeResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvgProcessingTimeResultRepository extends JpaRepository<AvgProcessingTimeResult, Long> {
    void deleteByWorkspaceId(Long workspaceId);
    void deleteByWorkspaceIdAndUserIdAndImportance(Long workspaceId, Long userId, Integer importance);
    List<AvgProcessingTimeResult> findByWorkspaceId(Long workspaceId);

    // 동일한 workspaceId, userId, importance를 기준으로 중복되는거 덮어씌움
    Optional<AvgProcessingTimeResult> findByWorkspaceIdAndUserIdAndImportance(Long workspaceId, Long userId, Integer importance);

}
