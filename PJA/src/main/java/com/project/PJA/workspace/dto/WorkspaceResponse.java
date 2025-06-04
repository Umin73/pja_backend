package com.project.PJA.workspace.dto;

import com.project.PJA.workspace.enumeration.ProgressStep;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceResponse {
    private Long workspaceId;
    private String projectName;
    private String teamName;
    private Long ownerId;
    private ProgressStep progressStep;

    public String getProgressStep() {
        return progressStep.getValue();
    }
}
