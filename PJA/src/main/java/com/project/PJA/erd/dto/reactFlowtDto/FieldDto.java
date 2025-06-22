package com.project.PJA.erd.dto.reactFlowtDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldDto {
    private String id;
    private String name;
    private String type;
    private boolean isPrimary;
    private boolean isForeign;
    private boolean isNullable;
}
