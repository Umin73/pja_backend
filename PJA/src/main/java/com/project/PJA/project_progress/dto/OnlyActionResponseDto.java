package com.project.PJA.project_progress.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnlyActionResponseDto {
    private Long actionId;
    private String actionName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long actionPostId;
}
