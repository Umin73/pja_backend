package com.project.PJA.erd.dto.reactFlowtDto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ERDFlowResponseDto {
    private List<TableNodeDto> tables;
    private List<EdgeDto> relations;
}
