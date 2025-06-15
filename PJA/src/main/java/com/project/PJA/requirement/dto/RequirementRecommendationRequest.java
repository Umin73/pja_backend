package com.project.PJA.requirement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequirementRecommendationRequest {
    @JsonProperty("project_overview")
    private String projectOverview;

    @JsonProperty("existing_requirements")
    private String existingRequirements;

    @JsonProperty("additional_count")
    private int additionalCount;

    @JsonProperty("max_tokens")
    private int maxTokens;

    @JsonProperty("temperature")
    private double temperature;

    @JsonProperty("model")
    private String model;

    @Builder
    public RequirementRecommendationRequest(String projectOverview, String existingRequirements) {
        this.projectOverview = projectOverview;
        this.existingRequirements = existingRequirements;
        this.additionalCount = 5;
        this.maxTokens = 4000;
        this.temperature = 0.3;
        this.model = "gpt-4o-mini";
    }
}
