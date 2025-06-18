package com.project.PJA.project_progress.dto.aiDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendedAction {
    @JsonProperty("name")
    private String name;

    @JsonProperty("importance")
    private Integer importance;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;
}
