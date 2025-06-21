package com.project.PJA.project_progress.dto.aiDto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionAiRequestDto {
    private String project_list;
    private Long max_tokens;
    private Double temperature;
    private String model;
}
