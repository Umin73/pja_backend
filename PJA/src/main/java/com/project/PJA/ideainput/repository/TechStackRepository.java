package com.project.PJA.ideainput.repository;

import com.project.PJA.ideainput.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    List<TechStack> findAllByIdeaInput_IdeaInputId(Long ideaInputId);
    void deleteAllByIdeaInput_IdeaInputId(Long ideaInputId);
}
