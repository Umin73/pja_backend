package com.project.PJA.erd.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErdColumnResponseDto {
    private String columnId;
    private String tableId;
    private String columnName;
    private String columnType;
    private boolean primaryKey;
    private boolean foreignKey;
    private boolean nullable;
}
