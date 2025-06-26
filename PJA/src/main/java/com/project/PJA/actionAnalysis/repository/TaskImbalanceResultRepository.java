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

    Optional<TaskImbalanceResult> findByWorkspaceIdAndUserIdAndStateAndImportance(Long workspaceId, Long userId, Progress state, Integer importance);


    // 동일한 workspaceId, userId, state, importance를 기준으로 중복되는거 덮어씌움
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

    @Query("""
        SELECT DISTINCT new com.project.PJA.actionAnalysis.dto.AssigneeDto(
            wm.user.userId,
            wm.user.name
        )
        FROM ActionParticipant ap
        JOIN ap.workspaceMember wm
        WHERE wm.workspace.workspaceId = :workspaceId
    """)
    List<AssigneeDto> findDistinctAssigneesByWorkspace(@Param("workspaceId") Long workspaceId);

//    boolean existsByWorkspaceIdAndUserIdAndImportanceAndStateAndAnalyzedAt(
//            Long workspaceId,
//            Long userId,
//            Integer importance,
//            Progress state,
//            LocalDateTime analyzedAt
//    );
}
