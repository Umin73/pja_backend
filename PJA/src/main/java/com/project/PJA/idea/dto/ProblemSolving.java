package com.project.PJA.idea.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSolving {
    @JsonProperty("current_problem")
    private String currentProblem;

    @JsonProperty("solution_idea")
    private String solutionIdea;

    @JsonProperty("expected_benefits")
    private List<String> expectedBenefits;
}
