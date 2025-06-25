package com.project.PJA.actionAnalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TaskImbalanceResponseDto {
    private List<TaskImbalanceGraphDto> graphData;
    private List<AssigneeDto> assignees;
}
