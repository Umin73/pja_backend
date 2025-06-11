package com.project.PJA.idea.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSolving {
    private String currentProblem;
    private String solutionIdea;
    private List<String> expectedBenefits;
}
