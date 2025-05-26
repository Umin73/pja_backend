package com.project.PJA.workspace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "workspace")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Long workspaceId;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "FK_WORKSPACE_USER"))
    //@OnDelete(action = OnDeleteAction.CASCADE)
    //private User user;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @CreatedDate
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
    private String project_summary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "project_features", columnDefinition = "json", nullable = true)
    private Map<String, Object> projectFeatures;
}
