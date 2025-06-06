package com.project.PJA.requirement.repository;


import com.project.PJA.requirement.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequirementRepository extends JpaRepository<Requirement, Long> {
    List<Requirement> findByWorkspace_WorkspaceId(Long workspaceId);
}
