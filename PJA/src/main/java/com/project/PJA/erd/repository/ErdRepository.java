package com.project.PJA.erd.repository;

import com.project.PJA.erd.entity.Erd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ErdRepository extends JpaRepository<Erd, Long> {
    Optional<Erd> findByWorkspaceId(Long workspaceId);
    boolean existsByWorkspaceId(Long workspaceId);
}
