package com.project.PJA.idea.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryDto {
    @JsonProperty("title")
    private String title;

    @JsonProperty("category")
    private String category;

    @JsonProperty("target_users")
    private List<String> targetUsers;

    @JsonProperty("main_purpose")
    private String mainPurpose;

    @JsonProperty("key_features")
    private List<KeyFeature> keyFeatures;

    @JsonProperty("core_technologies")
    private List<CoreTechnology> coreTechnologies;

    @JsonProperty("problem_solving")
    private ProblemSolving problemSolving;

    @JsonProperty("special_features")
    private List<String> specialFeatures;

    @JsonProperty("business_model")
    private BusinessModel businessModel;

    @JsonProperty("scalability")
    private Map<String, Object> scalability;

    @JsonProperty("development_timeline")
    private DevelopmentTimeline developmentTimeline;

    @JsonProperty("success_metrics")
    private List<String> successMetrics;

    @JsonProperty("challenges_and_risks")
    private List<ChallengeAndRisk> challengesAndRisks;
}
