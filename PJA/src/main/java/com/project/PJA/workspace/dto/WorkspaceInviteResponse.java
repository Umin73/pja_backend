package com.project.PJA.workspace.dto;

import com.project.PJA.workspace.enumeration.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceInviteResponse {
    private List<String> invitedEmails;
    private WorkspaceRole role;
}
