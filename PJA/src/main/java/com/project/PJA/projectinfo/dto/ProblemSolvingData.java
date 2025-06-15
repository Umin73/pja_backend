package com.project.PJA.projectinfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemSolvingData {
    @JsonProperty("current_problem")
    private String currentProblem;

    @JsonProperty("solution_idea")
    private String solutionIdea;

    @JsonProperty("expected_benefits")
    private List<String> expectedBenefits;
}
