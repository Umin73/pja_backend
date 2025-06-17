package com.project.PJA.project_progress.dto.aiDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiRequestAction {
    private Long actionId;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer importance;
}
