package com.project.PJA.idea.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiProjectSummaryData {
    @JsonProperty("title")
    private String title;

    @JsonProperty("category")
    private String category;

    @JsonProperty("target_users")
    private List<String> targetUsers;

    @JsonProperty("core_features")
    private List<String> coreFeatures;

    @JsonProperty("technology_stack")
    private List<String> technologyStack;

    @JsonProperty("problem_solving")
    private AiProblemSolving problemSolving;
}
