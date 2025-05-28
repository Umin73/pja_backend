package com.project.PJA.workspace.entity;

import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.enumeration.WorkspaceRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "workspace_member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_member_id", nullable = false)
    private Long workspaceMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false, foreignKey = @ForeignKey(name = "FK_WORKSPACE_MEMBER_WORKSPACE"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_WORKSPACE_MEMBER_USER"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users user;

    @Column(name = "workspace_role", nullable = false)
    private WorkspaceRole workspaceRole;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Builder
    public WorkspaceMember(Workspace workspace, Users user, WorkspaceRole workspaceRole) {
        this.workspace = workspace;
        this.user = user;
        this.workspaceRole = workspaceRole;
        this.joinedAt = LocalDateTime.now();
    }
}
