package com.project.PJA.ideainput.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IdeaInputData {
    private Long ideaInputId;
    private String projectName;
    private String projectTarget;
    private String projectDescription;
}
