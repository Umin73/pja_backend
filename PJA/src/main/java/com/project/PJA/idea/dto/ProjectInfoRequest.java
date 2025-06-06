package com.project.PJA.idea.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoRequest {
    private String projectName;
    private String projectTarget;
    private List<String> mainFunction;
    private List<String> techStack;
    private List<String> projectDescription;
}
