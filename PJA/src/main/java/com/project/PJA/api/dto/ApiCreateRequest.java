package com.project.PJA.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiCreateRequest {
    @JsonProperty("project_overview")
    private String projectOverview;

    @JsonProperty("requirements")
    private String requirements;

    @JsonProperty("project_summury")
    private String projectSummury;

    @JsonProperty("max_tokens")
    private int maxTokens;

    @JsonProperty("temperature")
    private double temperature;

    @JsonProperty("model")
    private String model;

    @Builder
    public ApiCreateRequest(String projectOverview, String requirements, String projectSummury) {
        this.projectOverview = projectOverview;
        this.requirements = requirements;
        this.projectSummury = projectSummury;
        this.maxTokens = 4000;
        this.temperature = 0.3;
        this.model = "gpt-4o";
    }
}
