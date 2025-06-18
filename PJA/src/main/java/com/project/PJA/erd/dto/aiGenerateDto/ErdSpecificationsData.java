package com.project.PJA.erd.dto.aiGenerateDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErdSpecificationsData {
    @JsonProperty("erd_tables")
    private List<AiErdTable> erdTables;
    @JsonProperty("erd_relationships")
    private List<AiErdRelationships> erdRelationships;
}
