package com.project.PJA.requirement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequirementRecommendationResponse {
    private List<RequirementRequest> requirements;
    private String model;
    private int total_tokens;
    private int prompt_tokens;
    private int completion_tokens;
}
