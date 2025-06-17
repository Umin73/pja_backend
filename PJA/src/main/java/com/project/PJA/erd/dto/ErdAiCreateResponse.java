package com.project.PJA.erd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.PJA.api.dto.ApiSpecifications;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErdAiCreateResponse {
    @JsonProperty("json")
    private ErdSpecifications json;

    @JsonProperty("model")
    private String model;

    @JsonProperty("total_tokens")
    private int totalTokens;

    @JsonProperty("prompt_tokens")
    private int promptTokens;

    @JsonProperty("completion_tokens")
    private int completionTokens;
}
