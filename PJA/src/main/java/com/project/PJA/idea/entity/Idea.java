package com.project.PJA.idea.entity;

import com.project.PJA.workspace.entity.Workspace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_summary")
public class Idea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_summary_id")
    private Long projectSummaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false, foreignKey = @ForeignKey(name = "FK_IDEA_WORKSPACE"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workspace workspace;

    @Column(name = "title")
    private String title;

    @Column(name = "category")
    private String category;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "target_users", columnDefinition = "jsonb")
    private List<String> targetUsers;

    @Column(name = "main_purpose", columnDefinition = "text")
    private String mainPurpose;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "key_features", columnDefinition = "jsonb")
    private List<KeyFeature> keyFeatures;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "core_technologies", columnDefinition = "jsonb")
    private List<CoreTechnology> coreTechnologies;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "problem_solving", columnDefinition = "jsonb")
    private ProblemSolving problemSolving;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "special_features", columnDefinition = "jsonb")
    private List<String> specialFeatures;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "business_model", columnDefinition = "jsonb")
    private BusinessModel businessModel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scalability", columnDefinition = "jsonb")
    private Map<String, Object> scalability;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "development_timeline", columnDefinition = "jsonb")
    private DevelopmentTimeline developmentTimeline;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "success_metrics", columnDefinition = "jsonb")
    private List<String> successMetrics;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "challenges_and_risks", columnDefinition = "jsonb")
    private List<ChallengeAndRisk> challengesAndRisks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KeyFeature {
        private String feature;
        private String description;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CoreTechnology {
        private String category;
        private List<String> technologies;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProblemSolving {
        private String currentProblem;
        private String solutionApproach;
        private List<String> expectedBenefits;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BusinessModel {
        private String type;
        private List<String> revenueStreams;
        private String targetMarket;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DevelopmentTimeline {
        private String estimatedDuration;
        private List<KeyMilestone> keyMilestones;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class KeyMilestone {
            private String phase;
            private String duration;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChallengeAndRisk {
        private String challenge;
        private String mitigation;
    }
}
