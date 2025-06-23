package com.project.PJA.project_progress.repository;

import com.project.PJA.project_progress.entity.Action;
import com.project.PJA.project_progress.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActionRepository extends JpaRepository<Action, Long> {
    // Feature 내에 가장 order가 큰 Action 찾아주는 쿼리
    Optional<Action> findTopByFeatureOrderByOrderIndexDesc(Feature feature);
    // Feature로 Action 리스트 찾기
    List<Action> findActionsByFeature(Feature feature);
    List<Action> findByFeatureOrderByOrderIndexAsc(Feature feature);
    // workspaceId로 Action 리스트 찾기
    List<Action> findByFeature_Workspace_WorkspaceId(Long workspaceId);

    // 진행중(BEFORE, IN_PROGRESS)인 내 작업(ACTION)들 가져오기(마감일이 빠른게 먼저 오도록)
    @Query("""
        SELECT a FROM Action a
        JOIN a.participants ap
        WHERE a.workspace.workspaceId = :workspaceId
          AND ap.workspaceMember.user.userId = :userId
          AND a.state IN (com.project.PJA.project_progress.entity.Progress.BEFORE, com.project.PJA.project_progress.entity.Progress.IN_PROGRESS)
        ORDER BY 
          CASE WHEN a.endDate IS NULL THEN 1 ELSE 0 END,
          a.endDate ASC
    """)
    List<Action> findAllByWorkspaceIdAndUserIdAndStateIsBeforeOrInProgressOrderByEndDateAsc(
            @Param("workspaceId") Long workspaceId,
            @Param("userId") Long userId
    );

    // 참여중인 전체 Action 개수 조회
    @Query("""
        SELECT COUNT(a) FROM Action a
        JOIN a.participants ap
        WHERE a.workspace.workspaceId = :workspaceId
          AND ap.workspaceMember.user.userId = :userId
    """)
    long countAllActionsByWorkspaceAndUser(@Param("workspaceId") Long workspaceId, @Param("userId") Long userId);

    // 참여중이면서 state가 DONE인 Action의 갯수 조회
    @Query("""
        SELECT COUNT(a) FROM Action a
        JOIN a.participants ap
        WHERE a.workspace.workspaceId = :workspaceId
          AND ap.workspaceMember.user.userId = :userId
          AND a.state = com.project.PJA.project_progress.entity.Progress.DONE
    """)
    long countDoneActionsByWorkspaceAndUser(@Param("workspaceId") Long workspaceId, @Param("userId") Long userId);

}
