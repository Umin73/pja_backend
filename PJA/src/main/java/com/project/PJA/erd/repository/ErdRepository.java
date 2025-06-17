package com.project.PJA.erd.repository;

import com.project.PJA.erd.entity.Erd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErdRepository extends JpaRepository<Erd, Long> {
    List<Erd> findByWorkspaceId_WorkspaceId(Long workspaceId);
    boolean existsByWorkspaceId_WorkspaceId(Long workspaceId);
}
