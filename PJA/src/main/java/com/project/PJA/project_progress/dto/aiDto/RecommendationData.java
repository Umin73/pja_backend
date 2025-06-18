package com.project.PJA.project_progress.dto.aiDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationData {
    private Long workspaceId;
    private Long categoryId;
    private Long featureId;
    private List<RecommendedAction> recommendedActions;
}