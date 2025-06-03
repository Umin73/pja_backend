package com.project.PJA.idea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryRequest {
    private String title;
    private String category;
    private List<String> targetUsers;
    private String mainPurpose;

    private List<KeyFeature> keyFeatures;
    private List<CoreTechnology> coreTechnologies;
    private ProblemSolving problemSolving;

    private List<String> specialFeatures;
    private BusinessModel businessModel;
    private Scalability scalability;
    private DevelopmentTimeline developmentTimeline;

    private List<String> successMetrics;
    private List<ChallengeAndRisk> challengesAndRisks;

    @Data
    public static class KeyFeature {
        private String feature;
        private String description;
    }

    @Data
    public static class CoreTechnology {
        private String category;
        private List<String> technologies;
    }

    @Data
    public static class ProblemSolving {
        private String currentProblem;
        private String solutionApproach;
        private List<String> expectedBenefits;
    }

    @Data
    public static class BusinessModel {
        private String type;
        private List<String> revenueStreams;
        private String targetMarket;
    }

    @Data
    public static class Scalability {
        private String userCapacity;
        private String expansionPlan;
        private String integrationCapability;
    }

    @Data
    public static class DevelopmentTimeline {
        private String estimatedDuration;
        private List<KeyMilestone> keyMilestones;
    }

    @Data
    public static class KeyMilestone {
        private String phase;
        private String duration;
    }

    @Data
    public static class ChallengeAndRisk {
        private String challenge;
        private String mitigation;
    }
}
