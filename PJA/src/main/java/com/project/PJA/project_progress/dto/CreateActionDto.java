package com.project.PJA.project_progress.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class CreateActionDto {
    @NotBlank
    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull
    private String state; // "IN_PROGRESS" 같은 문자열

    @Min(1) @Max(5)
    private Integer importance;

    private Boolean hasTest;

    private Set<Long> participantsId;
}
