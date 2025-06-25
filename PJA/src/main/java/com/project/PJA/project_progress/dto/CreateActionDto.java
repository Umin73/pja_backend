package com.project.PJA.project_progress.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.project.PJA.common.service.FlexibleLocalDateTimeDeserializer;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CreateActionDto {

    @NotBlank
    private String name;

    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    private LocalDateTime startDate;

    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    private LocalDateTime endDate;

    @NotNull
    private String state; // 문자열로 받아서 enum으로 변환 (BEFORE, IN_PROGRESS, DONE)

    private Boolean hasTest;

    @Min(0) @Max(5)
    private Integer importance;

    private List<Long> participantsId; // worksapceMember의 id 목록
}
