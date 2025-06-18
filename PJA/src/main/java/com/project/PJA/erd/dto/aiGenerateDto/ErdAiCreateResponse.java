package com.project.PJA.erd.dto.aiGenerateDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErdAiCreateResponse {
    @JsonProperty("json")
    private ErdJson json;

    @JsonProperty("model")
    private String model;

    @JsonProperty("total_tokens")
    private int totalTokens;

    @JsonProperty("prompt_tokens")
    private int promptTokens;

    @JsonProperty("completion_tokens")
    private int completionTokens;
}
