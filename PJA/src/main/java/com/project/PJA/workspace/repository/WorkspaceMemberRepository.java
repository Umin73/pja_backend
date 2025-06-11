package com.project.PJA.workspace.repository;

import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.enumeration.WorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
    List<WorkspaceMember> findAllByUser_UserId(Long userId);
    List<WorkspaceMember> findAllByWorkspace_WorkspaceId(Long workspaceId);
    Set<WorkspaceMember> findAllByWorkspace_WorkspaceIdAndWorkspaceRoleNot(Long workspaceId, WorkspaceRole workspaceRole);
    Boolean existsByWorkspace_WorkspaceIdAndUser_UserId(Long workspaceId, Long userId);
    Optional<WorkspaceMember> findByWorkspace_WorkspaceIdAndUser_UserId(Long workspaceId, Long userId);
}
