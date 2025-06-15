package com.project.PJA.projectinfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoWrapper {
    @JsonProperty("project_info")
    private ProjectInfoData projectInfo;
}
