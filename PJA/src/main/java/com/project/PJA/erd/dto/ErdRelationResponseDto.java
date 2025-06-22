package com.project.PJA.erd.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErdRelationResponseDto {
    private String relationId;
    private String relationType;
    private String foreignKeyName;
    private String constraintName;
    private String fkId;
    private String rfId;
    private ErdTableResponseDto fromTable;
    private ErdTableResponseDto toTable;
}
