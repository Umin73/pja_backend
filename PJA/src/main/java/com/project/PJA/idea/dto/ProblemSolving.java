package com.project.PJA.idea.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSolving {
    //@JsonProperty("current_problem")
    private String currentProblem;

    //@JsonProperty("solution_idea")
    private String solutionIdea;

    //@JsonProperty("expected_benefits")
    private List<String> expectedBenefits;
}
