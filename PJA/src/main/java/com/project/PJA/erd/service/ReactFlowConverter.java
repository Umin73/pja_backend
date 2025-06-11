package com.project.PJA.erd.service;

import com.project.PJA.erd.dto.reactFlowtDto.ERDFlowResponseDto;
import com.project.PJA.erd.dto.reactFlowtDto.EdgeDto;
import com.project.PJA.erd.dto.reactFlowtDto.FieldDto;
import com.project.PJA.erd.dto.reactFlowtDto.TableNodeDto;
import com.project.PJA.erd.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReactFlowConverter {

    public ERDFlowResponseDto convertToFlowResponse(Erd erd) {
        List<TableNodeDto> tables = erd.getTables().stream()
                .map(this::toTableNodeDto)
                .toList();

        List<EdgeDto> relations = erd.getTables().stream()
                .flatMap(t -> t.getFromRelationships().stream())
                .map(this::toEdgeDto)
                .toList();

        ERDFlowResponseDto dto = new ERDFlowResponseDto();
        dto.setTables(tables);
        dto.setRelations(relations);
        return dto;
    }

    private TableNodeDto toTableNodeDto(ErdTable table) {
        TableNodeDto dto = new TableNodeDto();
        dto.setId(table.getName());
        dto.setTableName(table.getName());

        List<FieldDto> fields = table.getColumns().stream()
                .map(this::toFieldDto)
                .toList();

        dto.setFields(fields);
        return dto;
    }

    private FieldDto toFieldDto(ErdColumn column) {
        FieldDto dto = new FieldDto();
        dto.setName(column.getName());
        dto.setType(column.getDataType());
        dto.setPrimary(column.isPrimaryKey());
        dto.setForeign(column.isForeignKey());
        dto.setNullable(column.isNullable());
        return dto;
    }

    private EdgeDto toEdgeDto(ErdRelationships relationships) {
        ErdColumn fk = relationships.getForeignColumn();
        ErdTable toTable = relationships.getToTable();

        String source = relationships.getFromTable().getName();
        String target = toTable.getName();
        String sourceHandle = "source-" + fk.getName();

        // 대상 테이블의 PK 컬럼 찾기
        String targetHandle = "target-" + toTable.getColumns().stream()
                .filter(ErdColumn::isPrimaryKey)
                .findFirst()
                .map(ErdColumn::getName)
                .orElse("id");

        String label = switch (relationships.getType()) {
            case ONE_TO_ONE -> "1:1";
            case ONE_TO_MANY -> "1:N";
            case MANY_TO_ONE -> "N:1";
            case MANY_TO_MANY -> "N:M";
            case SELF_REFERENCE -> "SELF";
            case INHERITANCE -> "ISA";
        };

        EdgeDto dto = new EdgeDto();
        dto.setId("edge-" + relationships.getErdRelationshipsId());
        dto.setSource(source);
        dto.setTarget(target);
        dto.setSourceHandle(sourceHandle);
        dto.setTargetHandle(targetHandle);
        dto.setLabel(label);
        return dto;
    }
}
