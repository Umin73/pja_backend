package com.project.PJA.ideainput.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdeaInputRequest {
    private String projectName;
    private String projectTarget;
    private List<MainFunctionData> mainFunction;
    private List<TechStackData> techStack;
    private String projectDescription;
}
