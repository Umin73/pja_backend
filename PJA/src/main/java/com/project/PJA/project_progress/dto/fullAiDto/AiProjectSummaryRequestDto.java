package com.project.PJA.project_progress.dto.fullAiDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiProjectSummaryRequestDto {
    @JsonProperty("project_summary")
    private String projectSummary;
}
