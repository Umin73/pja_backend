package com.project.PJA.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceUpdateRequest {
    private String projectName;
    private String teamName;
    private Boolean isPublic;
}
