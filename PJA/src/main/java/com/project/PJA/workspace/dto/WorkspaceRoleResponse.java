package com.project.PJA.workspace.dto;

import com.project.PJA.workspace.enumeration.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceRoleResponse {
    private WorkspaceRole role;
}
