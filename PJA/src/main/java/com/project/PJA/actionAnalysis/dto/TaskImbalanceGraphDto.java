package com.project.PJA.actionAnalysis.dto;

import com.project.PJA.project_progress.entity.Progress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskImbalanceGraphDto {
    private Long memberId;
    private String username;
    private Progress state;
    private Integer importance;
    private Long taskCount;
}
