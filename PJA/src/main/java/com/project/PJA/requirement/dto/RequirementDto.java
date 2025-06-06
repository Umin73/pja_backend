package com.project.PJA.requirement.dto;

import com.project.PJA.requirement.enumeration.RequirementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequirementDto {
    private Long requirementId;
    private RequirementType requirementType;
    private String content;
}
