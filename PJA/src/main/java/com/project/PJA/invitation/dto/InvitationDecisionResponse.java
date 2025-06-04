package com.project.PJA.invitation.dto;

import com.project.PJA.workspace.enumeration.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDecisionResponse {
    private Long workspaceId;
    private String invitedEmail;
    private WorkspaceRole role;
}
