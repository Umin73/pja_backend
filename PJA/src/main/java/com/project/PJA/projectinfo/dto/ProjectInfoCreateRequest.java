package com.project.PJA.projectinfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoCreateRequest {
    @JsonProperty("project_overview")
    private String projectOverview;

    @JsonProperty("requirements")
    private String requirements;

    @JsonProperty("additional_count")
    private int additionalCount;

    @JsonProperty("max_tokens")
    private int maxTokens;

    @JsonProperty("temperature")
    private double temperature;

    @JsonProperty("model")
    private String model;

    @Builder
    public ProjectInfoCreateRequest(String projectOverview, String requirements) {
        this.projectOverview = projectOverview;
        this.requirements = requirements;
        this.additionalCount = 5;
        this.maxTokens = 4000;
        this.temperature = 0.3;
        this.model = "gpt-4o-mini";
    }
}
