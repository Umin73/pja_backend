package com.project.PJA.project_progress.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CreateCategoryAndFeatureDto {
    @NotBlank
    private String name;

    private Boolean state;

    private Boolean hasTest;
}
