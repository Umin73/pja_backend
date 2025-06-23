package com.project.PJA.project_progress.dto.fullAiDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiFeatureDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("actions")
    private List<AiActionDto> actions;
}
