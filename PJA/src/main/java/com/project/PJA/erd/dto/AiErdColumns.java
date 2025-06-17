package com.project.PJA.erd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiErdColumns {
    @JsonProperty("name")
    private String name;

    @JsonProperty("data_type")
    private String dataType;

    @JsonProperty("is_primary_key")
    private boolean isPrimaryKey;

    @JsonProperty("is_foreign_key")
    private boolean isForeignKey;

    @JsonProperty("is_nullable")
    private boolean isNullable;
}
