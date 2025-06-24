package com.project.PJA.project_progress.dto;

import com.project.PJA.project_progress.entity.Progress;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CreateActionDto {

    @NotBlank
    private String name = "";

    private LocalDateTime startDate = LocalDateTime.now();

    private LocalDateTime endDate = LocalDateTime.now();

    @NotNull
    private String state = Progress.BEFORE.toString(); // 문자열로 받아서 enum으로 변환 (BEFORE, IN_PROGRESS, DONE)

    private Boolean hasTest = false;

    @Min(0) @Max(5)
    private Integer importance = 0;

    private List<Long> participantsIds = new ArrayList<>(); // worksapceMember의 id 목록
}
