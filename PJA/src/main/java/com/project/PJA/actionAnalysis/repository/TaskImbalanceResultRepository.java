package com.project.PJA.actionAnalysis.repository;

import com.project.PJA.actionAnalysis.dto.TaskImbalanceGraphDto;
import com.project.PJA.actionAnalysis.entity.TaskImbalanceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskImbalanceResultRepository extends JpaRepository<TaskImbalanceResult, Long> {

    @Query("""
            SELECT new com.project.PJA.actionAnalysis.dto.TaskImbalanceGraphDto(
                t.state, t.importance, SUM(t.taskCount)
            )
            FROM TaskImbalanceResult t
            WHERE t.workspaceId = :workspaceId AND t.analyzedAt = (
                SELECT MAX(t2.analyzedAt) FROM TaskImbalanceResult t2 WHERE t2.workspaceId = :workspaceId
            )
            GROUP BY t.state, t.importance
        """)
    List<TaskImbalanceGraphDto> findLatestGroupedByStateAndImportance(@Param("workspaceId") Long workspaceId);

}
