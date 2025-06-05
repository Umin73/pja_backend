package com.project.PJA.workspace.repository;

import com.project.PJA.workspace.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
    List<WorkspaceMember> findAllByUser_UserId(Long userId);
    Boolean existsByWorkspace_WorkspaceIdAndUser_UserId(Long workspaceId, Long userId);
    WorkspaceMember findByWorkspaceIdAndUser_UserId(Long workspaceId, Long userId);
}
