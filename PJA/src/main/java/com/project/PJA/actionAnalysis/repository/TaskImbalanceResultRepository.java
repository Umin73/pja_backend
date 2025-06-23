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
        wm.workspaceMemberId,
        wm.user.name,
        t.state,
        t.importance,
        SUM(t.taskCount)
    )
    FROM TaskImbalanceResult t
    JOIN WorkspaceMember wm
        ON wm.user.userId = t.userId
        AND wm.workspace.workspaceId = t.workspaceId
    WHERE t.workspaceId = :workspaceId
      AND t.analyzedAt = (
        SELECT MAX(t2.analyzedAt)
        FROM TaskImbalanceResult t2
        WHERE t2.workspaceId = :workspaceId
      )
    GROUP BY wm.workspaceMemberId, wm.user.name, t.state, t.importance
""")
    List<TaskImbalanceGraphDto> findLatestGroupedByWorkspaceMember(@Param("workspaceId") Long workspaceId);

}
