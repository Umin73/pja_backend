package com.project.PJA.project_progress.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class UpdateActionDto {
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String state;
    private Integer importance;
    private Integer orderIndex;
    private Boolean hasTest;
    private Set<Long> participantIds;
}
