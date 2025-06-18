package com.project.PJA.erd.dto.aiGenerateDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiErdRelationships {
    @JsonProperty("from_table")
    private String fromTable;

    @JsonProperty("to_table")
    private String toTable;

    @JsonProperty("relationship_type")
    private String relationshipType;

    @JsonProperty("foreign_key")
    private String foreignKey;

    @JsonProperty("constraint_name")
    private String constraintName;
}
