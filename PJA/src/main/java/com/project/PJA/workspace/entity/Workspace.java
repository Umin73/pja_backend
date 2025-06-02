package com.project.PJA.workspace.entity;

import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.enumeration.ProgressStep;
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
import java.util.Map;

@Entity
@Table(name = "workspace")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "FK_WORKSPACE_USER"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users user;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_shared_agree", nullable = false)
    private Boolean isPublic = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "progress_step", nullable = false, length = 10)
    private ProgressStep progressStep = ProgressStep.ZERO;

    @Column(name = "project_target", nullable = true)
    private String projectTarget;

    @Column(name = "project_description", columnDefinition = "TEXT", nullable = true)
    private String projectDescription;

    @Column(name = "project_summary", columnDefinition = "TEXT", nullable = true)
    private String projectSummary;

    // @Column(name = "project_features", columnDefinition = "json", nullable = true)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "project_features", nullable = true)
    private Map<String, Object> projectFeatures;

    @Builder
    public Workspace(Users user, String projectName, String teamName, Boolean isPublic) {
        this.user = user;
        this.projectName = projectName;
        this.teamName = teamName;
        this.isPublic = (isPublic == null) ? false : isPublic;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String projectName, String teamName) {
        this.projectName = projectName;
        this.teamName = teamName;
    }

    public void updateIsCompleted(ProgressStep progressStep) {
        this.progressStep = progressStep;
    }
}
