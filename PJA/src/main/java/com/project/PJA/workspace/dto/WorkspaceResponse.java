package com.project.PJA.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceResponse {
    private Long id;
    private String projectName;
    private String teamName;
    //private Long ownerId;
    private Boolean isCompleted;
}
