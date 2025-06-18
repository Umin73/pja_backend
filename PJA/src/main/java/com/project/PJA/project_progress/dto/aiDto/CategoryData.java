package com.project.PJA.project_progress.dto.aiDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryData {
    private Long categoryId;
    private String categoryName;
    private FeatureData featureData;
}
