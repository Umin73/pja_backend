package com.project.PJA.git_interlocking.repository;

import com.project.PJA.git_interlocking.entity.GitInterlocking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GitInterlockingRepository extends JpaRepository<GitInterlocking, Integer> {
    Optional<GitInterlocking> findByWorkspace_WorkspaceId(Long workspaceId);
}
