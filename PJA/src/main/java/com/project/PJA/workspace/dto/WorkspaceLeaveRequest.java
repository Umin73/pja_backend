package com.project.PJA.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceLeaveRequest {
    private Long workspaceId;
    private String projectName;
    private String teamName;
}
