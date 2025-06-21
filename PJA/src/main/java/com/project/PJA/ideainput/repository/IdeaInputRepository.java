package com.project.PJA.ideainput.repository;

import com.project.PJA.ideainput.entity.IdeaInput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface IdeaInputRepository extends JpaRepository<IdeaInput, Long> {
    Optional<IdeaInput> findByWorkspace_WorkspaceId(Long workspaceId);
}
