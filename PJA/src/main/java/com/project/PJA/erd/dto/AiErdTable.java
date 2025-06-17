package com.project.PJA.erd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiErdTable {
    @JsonProperty("name")
    private String name;

    @JsonProperty("erd_columns")
    private List<AiErdColumns> erdColumns;
}
