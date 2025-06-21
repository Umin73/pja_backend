package com.project.PJA.project_progress.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionPostFileResponseDto {
    private String filePath;
    private String contentType;
}
