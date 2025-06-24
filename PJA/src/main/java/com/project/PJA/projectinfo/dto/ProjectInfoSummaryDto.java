package com.project.PJA.projectinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoSummaryDto {
    private Long project_info_id;
    private String title;
    private String category;
    private List<String> target_users;
    private List<String> core_features;
    private List<String> technology_stack;
    private ProblemSolving problem_solving;
    private String created_at;
    private String updated_at;
    private Long workspace_id;
}
