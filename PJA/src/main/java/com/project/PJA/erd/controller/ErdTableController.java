package com.project.PJA.erd.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.erd.dto.CreateErdTableDto;
import com.project.PJA.erd.entity.ErdTable;
import com.project.PJA.erd.service.ErdTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/workspace/")
@RequiredArgsConstructor
public class ErdTableController {

    private final ErdTableService erdTableService;

    @PostMapping("{workspaceId}/erd/{erdId}/table")
    public ResponseEntity<SuccessResponse<?>> createErdTable(@PathVariable("workspaceId") Long workspaceId,
                                                             @PathVariable("erdId") Long erdId,
                                                             @RequestBody CreateErdTableDto dto) {
        ErdTable createdErdTable = erdTableService.createErdTable(erdId, dto.getTableName());

        Map<String, Object> data = new HashMap<>();
        data.put("erdTableId", createdErdTable.getErdTableId());
        data.put("name", createdErdTable.getName());
        data.put("erdId", createdErdTable.getErd().getErdId());
        data.put("columns", createdErdTable.getColumns());
        data.put("fromRelationships", createdErdTable.getFromRelationships());
        data.put("toRelationships", createdErdTable.getToRelationships());

        SuccessResponse<?> response = new SuccessResponse("success", "ERD 테이블이 생성되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
