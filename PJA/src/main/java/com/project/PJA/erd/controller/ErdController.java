package com.project.PJA.erd.controller;


import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.erd.dto.reactFlowtDto.ERDFlowResponseDto;
import com.project.PJA.erd.entity.Erd;
import com.project.PJA.erd.service.ErdService;
import com.project.PJA.erd.service.ReactFlowConverter;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/workspaces/")
@RequiredArgsConstructor
public class ErdController {

    private final ErdService erdService;
    private final ReactFlowConverter reactFlowConverter;

    @PostMapping("{workspaceId}/erd")
    public ResponseEntity<SuccessResponse<?>> createErd(@AuthenticationPrincipal Users user,
                                                        @PathVariable Long workspaceId) {
        Erd createdErd = erdService.createErd(user, workspaceId);

        Map<String, Object> data = new HashMap<>();
        data.put("erdId", createdErd.getErdId());
        data.put("createdAt", createdErd.getCreatedAt());
        data.put("workspaceId", createdErd.getWorkspaceId());
        data.put("tables", createdErd.getTables());

        SuccessResponse<?> response = new SuccessResponse<>("success", "ERD가 생성되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("{workspaceId}/erd/{erdId}/flow")
    public ResponseEntity<SuccessResponse<?>> getFlow(@AuthenticationPrincipal Users user,
                                                      @PathVariable Long workspaceId,
                                                      @PathVariable Long erdId) {
        Erd erd = erdService.findByIdOrThrow(erdId);
        ERDFlowResponseDto dto = reactFlowConverter.convertToFlowResponse(erd);

        SuccessResponse<?> response = new SuccessResponse<>("success", "ERD 데이터 조회에 성공했습니다.", dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
