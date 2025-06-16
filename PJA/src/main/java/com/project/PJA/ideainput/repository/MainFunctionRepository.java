package com.project.PJA.ideainput.repository;

import com.project.PJA.ideainput.entity.MainFunction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MainFunctionRepository extends JpaRepository<MainFunction, Long> {
    List<MainFunction> findAllByIdeaInput_IdeaInputId(Long ideaInputId);
    void deleteAllByIdeaInput_IdeaInputId(Long ideaInputId);
}
