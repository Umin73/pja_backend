package com.project.PJA.projectinfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoData {
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
    private ProblemSolvingData problemSolving;
}
