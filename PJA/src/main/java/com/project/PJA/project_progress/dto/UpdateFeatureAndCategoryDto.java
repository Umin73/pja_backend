package com.project.PJA.project_progress.dto;

import com.project.PJA.project_progress.entity.Progress;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class UpdateFeatureAndCategoryDto {
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Progress state;
    private Integer importance;
    private Integer orderIndex;
    private Set<Long> participantIds;
}
