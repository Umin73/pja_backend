package com.project.PJA.requirement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequirementResponse {
    private Long workspaceId;
    private List<RequirementDto> requirements;
}
