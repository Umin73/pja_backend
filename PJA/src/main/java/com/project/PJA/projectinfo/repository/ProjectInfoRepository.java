package com.project.PJA.projectinfo.repository;

import com.project.PJA.projectinfo.entity.ProjectInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectInfoRepository extends JpaRepository<ProjectInfo, Long> {
    Optional<ProjectInfo> findByWorkspace_WorkspaceId(Long workspaceId);
    boolean existsByWorkspace_WorkspaceId(Long workspaceId);
    List<ProjectInfo> findAllByWorkspace_WorkspaceIdIn(List<Long> workspaceId);
}
