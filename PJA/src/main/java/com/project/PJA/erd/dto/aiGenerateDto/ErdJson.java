package com.project.PJA.erd.dto.aiGenerateDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErdJson {
    @JsonProperty("erd_tables")
    private List<AiErdTable> erdTables;

    @JsonProperty("erd_relationships")
    private List<AiErdRelationships> erdRelationships;
}
