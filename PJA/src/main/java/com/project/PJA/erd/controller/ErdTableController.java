package com.project.PJA.erd.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.erd.dto.CreateErdTableDto;
import com.project.PJA.erd.dto.ErdTableNameDto;
import com.project.PJA.erd.dto.ErdTableResponseDto;
import com.project.PJA.erd.entity.ErdTable;
import com.project.PJA.erd.service.ErdTableService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/workspaces/")
@RequiredArgsConstructor
public class ErdTableController {

    private final ErdTableService erdTableService;

    @PostMapping("{workspaceId}/erd/{erdId}/table")
    public ResponseEntity<SuccessResponse<?>> createErdTable(@AuthenticationPrincipal Users user,
                                                             @PathVariable("workspaceId") Long workspaceId,
                                                             @PathVariable("erdId") Long erdId,
                                                             @RequestBody CreateErdTableDto dto) {
        ErdTable createdErdTable = erdTableService.createErdTable(user, workspaceId, erdId, dto.getTableName());
        ErdTableResponseDto data = erdTableService.getErdTableDto(createdErdTable);

        SuccessResponse<?> response = new SuccessResponse("success", "ERD 테이블이 생성되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("{workspaceId}/erd/{erdId}/table/{tableId}")
    public ResponseEntity<SuccessResponse<?>> updateErdTableName(@AuthenticationPrincipal Users user,
                                                                 @PathVariable("workspaceId") Long workspaceId,
                                                                 @PathVariable("erdId") Long erdId,
                                                                 @PathVariable("tableId") String tableId,
                                                                 @RequestBody ErdTableNameDto dto) {
        ErdTable erdTable = erdTableService.updateErdTableName(user, workspaceId, erdId, tableId, dto);
        ErdTableResponseDto data = erdTableService.getErdTableDto(erdTable);
        SuccessResponse<?> response = new SuccessResponse<>("success", "ERD 테이블이 성공적으로 수정되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{workspaceId}/erd/{erdId}/table/{tableId}")
    public ResponseEntity<SuccessResponse<?>> deleteErdTable(@AuthenticationPrincipal Users user,
                                                             @PathVariable("workspaceId") Long workspaceId,
                                                             @PathVariable("erdId") Long erdId,
                                                             @PathVariable("tableId") String tableId) {
        erdTableService.deleteErdTable(user, workspaceId, erdId, tableId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "ERD 테이블이 성공적으로 삭제되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
