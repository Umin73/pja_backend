package com.project.PJA.project_progress.dto.aiDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActionRecommendationJson {

    @JsonProperty("workspaceId")
    private Long workspaceId;

    @JsonProperty("categoryId")
    private Long categoryId;

    @JsonProperty("featureId")
    private Long featureId;

    @JsonProperty("recommendedActions")
    private List<RecommendedAction> recommendedActions;
}
