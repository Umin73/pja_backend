package com.project.PJA.workspace.entity;

import com.project.PJA.workspace.enumeration.InvitationStatus;
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
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invitation")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_id", nullable = false)
    private Long invitationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false, foreignKey = @ForeignKey(name = "FK_INVITATION_WORKSPACE"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workspace workspace;

    @Column(name = "invited_email", nullable = false)
    private String invitedEmail;

    @Column(name = "workspace_role", nullable = false)
    private WorkspaceRole workspaceRole;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "invitation_status", nullable = false)
    private InvitationStatus invitationStatus = InvitationStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "accepted_at", nullable = true)
    private LocalDateTime acceptedAt;

    @Builder
    public Invitation(Workspace workspace, String invitedEmail, WorkspaceRole workspaceRole, String token) {
        this.workspace = workspace;
        this.invitedEmail = invitedEmail;
        this.workspaceRole = workspaceRole;
        this.token = token;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusHours(24);
    }
}
