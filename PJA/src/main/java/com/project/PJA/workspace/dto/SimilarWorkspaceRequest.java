package com.project.PJA.workspace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimilarWorkspaceRequest {
    @JsonProperty("project_info")
    private String projectInfo;

    @JsonProperty("top_k")
    private Integer topK;

    @Builder
    public SimilarWorkspaceRequest(String projectInfo) {
        this.projectInfo = projectInfo;
        this.topK = 10;
    }
}
