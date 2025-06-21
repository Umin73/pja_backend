package com.project.PJA.requirement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequirementData {
    private Long requirementId;
    private String requirementType;
    private String content;
}
