package com.project.PJA.invitation.dto;

import com.project.PJA.invitation.enumeration.InvitationStatus;
import com.project.PJA.workspace.enumeration.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationInfoResponse {
    private Long workspaceId;
    private String projectName;
    private String teamName;
    private String ownerName;
    private WorkspaceRole role;
    private InvitationStatus status;
}
