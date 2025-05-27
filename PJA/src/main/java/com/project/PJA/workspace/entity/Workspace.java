package com.project.PJA.workspace.entity;

import com.project.PJA.user.entity.Users;
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

    @Column(name = "is_shared_agree", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isSharedAgree = false;

    @Column(name = "is_completed", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isCompleted = false;

    @Column(name = "project_target", nullable = true)
    private String projectTarget;

    @Column(name = "project_description", columnDefinition = "TEXT", nullable = true)
    private String projectDescription;

    @Column(name = "project_summary", columnDefinition = "TEXT", nullable = true)
    private String projectSummary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "project_features", columnDefinition = "json", nullable = true)
    private Map<String, Object> projectFeatures;

    @Builder
    public Workspace(Users user, String projectName, String teamName, Boolean isSharedAgree) {
        this.user = user;
        this.projectName = projectName;
        this.teamName = teamName;
        this.isSharedAgree = (isSharedAgree = null) ? false : isSharedAgree;
        this.createdAt = LocalDateTime.now();
    }
}
