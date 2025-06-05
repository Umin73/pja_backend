package com.project.PJA.erd.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.erd.dto.ErdColumnRequestDto;
import com.project.PJA.erd.dto.ErdColumnResponseDto;
import com.project.PJA.erd.dto.ErdTableNameDto;
import com.project.PJA.erd.entity.ErdColumn;
import com.project.PJA.erd.service.ErdColumnService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/workspace/")
@RequiredArgsConstructor
public class ErdColumnController {

    private final ErdColumnService erdColumnService;

    @PostMapping("{workspaceId}/erd/{erdId}/table/{tableId}/column")
    public ResponseEntity<SuccessResponse<?>> createErdColumn(@AuthenticationPrincipal Users user,
                                                              @PathVariable("workspaceId") Long workspaceId,
                                                              @PathVariable("erdId") Long erdId,
                                                              @PathVariable("tableId") Long tableId,
                                                              @RequestBody ErdColumnRequestDto dto) {
        log.info("== ERD 컬럼 생성 API 진입 ==");
        ErdColumn erdColumn = erdColumnService.createErdColumn(user, workspaceId, erdId, tableId, dto);
        log.info("ERD 컬럼 생성 완료 됨: {}", erdColumn.getName());
        ErdColumnResponseDto data = erdColumnService.getErdColumnDto(erdColumn);
        log.info("ERD Response DTO: {}", data.getColumnName());

        SuccessResponse<?> response = new SuccessResponse<>("success", "ERD 컬럼이 생성되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("{workspaceId}/erd/{erdId}/table/{tableId}/column/{columnId}")
    public ResponseEntity<SuccessResponse<?>> updateErdColumn(@AuthenticationPrincipal Users user,
                                                                 @PathVariable("workspaceId") Long workspaceId,
                                                                 @PathVariable("erdId") Long erdId,
                                                                 @PathVariable("tableId") Long tableId,
                                                                 @PathVariable("columnId") Long columnId,
                                                                 @RequestBody ErdColumnRequestDto dto) {
        ErdColumn updatedErdColumn = erdColumnService.updateErdColumn(workspaceId, erdId, tableId, columnId, dto);
        ErdColumnResponseDto data = erdColumnService.getErdColumnDto(updatedErdColumn);
        SuccessResponse<?> response = new SuccessResponse<>("success", "ERD 컬럼이 성공적으로 수정되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{workspaceId}/erd/{erdId}/table/{tableId}/column/{columnId}")
    public ResponseEntity<SuccessResponse<?>> deleteErdColumn(@AuthenticationPrincipal Users user,
                                                              @PathVariable("workspaceId") Long workspaceId,
                                                              @PathVariable("erdId") Long erdId,
                                                              @PathVariable("tableId") Long tableId,
                                                              @PathVariable("columnId") Long columnId) {
        erdColumnService.deleteErdColumn(workspaceId, erdId, tableId, columnId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "ERD 테이블이 성공적으로 삭제되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
