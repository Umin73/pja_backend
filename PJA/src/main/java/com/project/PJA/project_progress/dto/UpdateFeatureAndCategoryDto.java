package com.project.PJA.project_progress.dto;

import lombok.Getter;


@Getter
public class UpdateFeatureAndCategoryDto {
    private String name;
    private Boolean state;
    private Integer orderIndex;
    private Boolean hasTest;
}
