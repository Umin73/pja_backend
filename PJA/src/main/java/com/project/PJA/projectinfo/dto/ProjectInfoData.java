package com.project.PJA.projectinfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
<<<<<<<< HEAD:PJA/src/main/java/com/project/PJA/projectinfo/dto/AiProjectSummaryData.java
public class AiProjectSummaryData {
========
public class ProjectInfoData {
>>>>>>>> develop:PJA/src/main/java/com/project/PJA/projectinfo/dto/ProjectInfoData.java
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
