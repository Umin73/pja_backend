package com.project.PJA.erd.dto.reactFlowtDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TableNodeDto {
    private String id;          // 테이블 ID (e.g., "users")
    private String tableName;
    private List<FieldDto> fields;
}
