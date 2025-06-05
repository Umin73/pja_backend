package com.project.PJA.erd.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.erd.dto.ErdColumnRequestDto;
import com.project.PJA.erd.dto.ErdColumnResponseDto;
import com.project.PJA.erd.dto.ErdTableNameDto;
import com.project.PJA.erd.entity.ErdColumn;
import com.project.PJA.erd.service.ErdColumnService;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.service.WorkspaceService;
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
    private final WorkspaceService workspaceService;

    @PostMapping("{workspaceId}/erd/{erdId}/table/{tableId}/column")
    public ResponseEntity<SuccessResponse<?>> createErdColumn(@AuthenticationPrincipal Users user,
                                                              @PathVariable("workspaceId") Long workspaceId,
                                                              @PathVariable("erdId") Long erdId,
                                                              @PathVariable("tableId") Long tableId,
                                                              @RequestBody ErdColumnRequestDto dto) {
        // GUEST는 삭제X
        // 멤버 권한 로직 작성 완료 시 추가 필요
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId,"게스트는 ERD 컬럼을 생성할 권한이 없습니다.");

        ErdColumn erdColumn = erdColumnService.createErdColumn(user, workspaceId, erdId, tableId, dto);
        ErdColumnResponseDto data = erdColumnService.getErdColumnDto(erdColumn);

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
        // GUEST는 삭제X
        // 멤버 권한 로직 작성 완료 시 추가 필요
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId,"게스트는 ERD 컬럼을 수정할 권한이 없습니다.");

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
        // GUEST는 삭제X
        // 멤버 권한 로직 작성 완료 시 추가 필요
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId,"게스트는 ERD 컬럼을 삭제할 권한이 없습니다.");

        erdColumnService.deleteErdColumn(workspaceId, erdId, tableId, columnId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "ERD 테이블이 성공적으로 삭제되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
