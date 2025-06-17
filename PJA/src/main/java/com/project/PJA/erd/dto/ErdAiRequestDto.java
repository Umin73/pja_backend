package com.project.PJA.erd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErdAiRequestDto {
    private String project_overview;
    private String requirements;
    private String project_summury;
    private Long max_tokens;
    private Double temperature;
    private String model;
}
