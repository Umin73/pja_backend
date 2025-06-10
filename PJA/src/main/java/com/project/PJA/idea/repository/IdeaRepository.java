package com.project.PJA.idea.repository;

import com.project.PJA.idea.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IdeaRepository extends JpaRepository<Idea, Long> {
    Optional<Idea> findByWorkspace_WorkspaceId(Long workspaceId);
}
