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
public class AiActionDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("importance")
    private Integer importance;
}
