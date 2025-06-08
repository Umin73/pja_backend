package com.project.PJA.project_progress.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CreateCategoryAndFeatureDto {

    @NotBlank
    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull
    private String state; // 문자열로 받아서 enum으로 변환 (BEFORE, IN_PROGRESS, DONE)

    @Min(1) @Max(5)
    private Integer importance;

    private List<Long> participantsId; // worksapceMember의 id 목록
}
