package com.project.PJA.actionAnalysis.repository;

import com.project.PJA.actionAnalysis.dto.AssigneeDto;
import com.project.PJA.actionAnalysis.dto.TaskImbalanceGraphDto;
import com.project.PJA.actionAnalysis.entity.TaskImbalanceResult;
import com.project.PJA.project_progress.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskImbalanceResultRepository extends JpaRepository<TaskImbalanceResult, Long> {

    void deleteByWorkspaceId(Long workspaceId);

//    // 동일한 workspaceId, userId, state, importance를 기준으로 중복되는거 덮어씌움
//    @Query("""
//    SELECT new com.project.PJA.actionAnalysis.dto.TaskImbalanceGraphDto(
//        wm.workspaceMemberId,
//        wm.user.name,
//        t.state,
//        t.importance,
//        SUM(t.taskCount)
//    )
//    FROM TaskImbalanceResult t
//    JOIN WorkspaceMember wm
//        ON wm.user.userId = t.userId
//        AND wm.workspace.workspaceId = t.workspaceId
//    WHERE t.workspaceId = :workspaceId
//      AND t.analyzedAt = (
//        SELECT MAX(t2.analyzedAt)
//        FROM TaskImbalanceResult t2
//        WHERE t2.workspaceId = :workspaceId
//      )
//    GROUP BY wm.workspaceMemberId, wm.user.name, t.state, t.importance
//""")
//    List<TaskImbalanceGraphDto> findLatestGroupedByWorkspaceMember(@Param("workspaceId") Long workspaceId);

    List<TaskImbalanceResult> findByWorkspaceId(@Param("workspaceId") Long workspaceId);
}
