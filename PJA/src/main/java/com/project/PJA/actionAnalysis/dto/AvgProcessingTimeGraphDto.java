package com.project.PJA.actionAnalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AvgProcessingTimeGraphDto {
    private Long userId;
    private Integer importance;
    private Long meanHours;
}
