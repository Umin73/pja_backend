package com.project.PJA.workspace_activity.repository;

import com.project.PJA.workspace_activity.entity.WorkspaceActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkspaceActivityRepository extends JpaRepository<WorkspaceActivity, Long> {

    void deleteByWorkspaceId(Long workspaceId);
    List<WorkspaceActivity> findByWorkspaceId(Long workspaceId);
    List<WorkspaceActivity> findByWorkspaceIdOrderByCreatedAtDesc(Long workspaceId);
}
