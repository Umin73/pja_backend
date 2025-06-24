package com.project.PJA.project_progress.dto;

import com.project.PJA.project_progress.entity.Progress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyActionDto {
    private Long actionId;
    private String actionName;
    private LocalDateTime endDate;
    private String state;
    private Integer importance;
}
