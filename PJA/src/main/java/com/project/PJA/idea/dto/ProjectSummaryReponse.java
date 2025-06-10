package com.project.PJA.idea.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryReponse {
    private Long ideaId;
    private String title;
    private String category;
    private List<String> targetUsers;
    private List<String> coreFeatures;
    private List<String> technologyStack;
    private ProblemSolving problemSolving;
}
