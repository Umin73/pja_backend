package com.project.PJA.erd.dto;

import lombok.Getter;

@Getter
public class ErdColumnRequestDto {
    private String columnName;
    private String dataType;
    private boolean primaryKey;
    private boolean foreignKey;
    private boolean nullable;
}
