package com.project.PJA.idea.repository;

import com.project.PJA.idea.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdeaRepository extends JpaRepository<Idea, Long> {
    Idea findByWorkspaceId(Long workspaceId);
}
