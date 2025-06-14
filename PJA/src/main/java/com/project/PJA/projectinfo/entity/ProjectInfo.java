package com.project.PJA.projectinfo.entity;

import com.project.PJA.projectinfo.dto.ProblemSolving;
import com.project.PJA.workspace.entity.Workspace;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_info")
public class ProjectInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_info_id")
    private Long projectInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false, foreignKey = @ForeignKey(name = "FK_IDEA_WORKSPACE"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workspace workspace;

    @Column(name = "title")
    private String title;

    @Column(name = "category")
    private String category;

    @Type(JsonType.class)
    @Column(name = "target_users", columnDefinition = "jsonb")
    private List<String> targetUsers;

    @Type(JsonType.class)
    @Column(name = "core_features", columnDefinition = "jsonb")
    private List<String> coreFeatures;

    @Type(JsonType.class)
    @Column(name = "technology_stack", columnDefinition = "jsonb")
    private List<String> technologyStack;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "problem_solving", columnDefinition = "jsonb")
    private ProblemSolving problemSolving;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ProjectInfo(Workspace workspace,
                       String title,
                       String category,
                       List<String> targetUsers,
                       List<String> coreFeatures,
                       List<String> technologyStack,
                       ProblemSolving problemSolving
    ) {
        this.workspace = workspace;
        this.title = title;
        this.category = category;
        this.targetUsers = targetUsers;
        this.coreFeatures = coreFeatures;
        this.technologyStack = technologyStack;
        this.problemSolving = problemSolving;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title,
                       String category,
                       List<String> targetUsers,
                       List<String> coreFeatures,
                       List<String> technologyStack,
                       ProblemSolving problemSolving
    ) {
        this.title = title;
        this.category = category;
        this.targetUsers = targetUsers;
        this.coreFeatures = coreFeatures;
        this.technologyStack = technologyStack;
        this.problemSolving = problemSolving;
        this.updatedAt = LocalDateTime.now();
    }
}
