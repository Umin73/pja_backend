package com.project.PJA.projectinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectData {
    private Long projectInfoId;
    private String title;
    private String category;
    private List<String> targetUsers;
    private List<String> coreFeatures;
    private List<String> technologyStack;
    private ProblemSolving problemSolving;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
