package com.project.PJA.idea.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryReponse {
    private Long ideaId;
    private Long workspaceId;

    private String title;
    private String category;
    private List<String> target_users;
    private String main_purpose;

    private List<KeyFeature> key_features;
    private List<CoreTechnology> core_technologies;
    private ProblemSolving problem_solving;

    private List<String> special_features;
    private BusinessModel business_model;
    private Map<String, Object> scalability;
    private DevelopmentTimeline development_timeline;

    private List<String> success_metrics;
    private List<ChallengeAndRisk> challenges_and_risks;
}
