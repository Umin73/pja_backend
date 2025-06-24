package com.project.PJA.erd.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.erd.dto.CreateErdRelationDto;
import com.project.PJA.erd.dto.ErdRelationResponseDto;
import com.project.PJA.erd.dto.UpdateRelationDto;
import com.project.PJA.erd.entity.ErdRelationships;
import com.project.PJA.erd.service.ErdRelationService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/workspaces/")
@RequiredArgsConstructor
public class ErdRelationController {

    private final ErdRelationService erdRelationService;

    @PostMapping("{workspaceId}/erd/{erdId}/relation")
    public ResponseEntity<SuccessResponse<?>> createRelation(@AuthenticationPrincipal Users user,
                                                             @PathVariable("workspaceId") Long workspaceId,
                                                             @PathVariable("erdId") Long erdId,
                                                             @RequestBody CreateErdRelationDto dto) {
        log.info("== ERD 관계 생성 API 진입, {}", dto);
        ErdRelationships relationships = erdRelationService.createRelation(user, workspaceId, erdId, dto);
        ErdRelationResponseDto data = erdRelationService.getRelationDto(relationships);

        SuccessResponse<?> response = new SuccessResponse<>("success", "관계가 생성되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("{workspaceId}/erd/{erdId}/relation/{relationId}")
    public ResponseEntity<SuccessResponse<?>> deleteRelation(@AuthenticationPrincipal Users user,
                                                             @PathVariable("workspaceId") Long workspaceId,
                                                             @PathVariable("erdId") Long erdId,
                                                             @PathVariable("relationId") String relationId) {
        log.info("== ERD 관계 삭제 API 진입 , relationId: {} ==",relationId);
        erdRelationService.deleteRelation(user, workspaceId, erdId, relationId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "관계가 삭제되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/erd/{erdId}/relation/{relationId}/type")
    ResponseEntity<SuccessResponse<?>> updateRelationType(@AuthenticationPrincipal Users user,
                                                          @PathVariable("workspaceId") Long workspaceId,
                                                          @PathVariable("erdId") Long erdId,
                                                          @PathVariable("relationId") String relationId,
                                                          @RequestBody UpdateRelationDto dto) {
        log.info("== ERD 관계 타입 수정 API 진입 ==");
        ErdRelationships data = erdRelationService.updateRelationType(user, workspaceId, erdId, relationId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "관계의 타입을 수정했습니다.", Map.of("relationId", relationId, "type",data.getType()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
