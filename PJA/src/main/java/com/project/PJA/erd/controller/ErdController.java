package com.project.PJA.erd.controller;


import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.erd.dto.aiGenerateDto.ErdAiCreateResponse;
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
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/workspaces/")
@RequiredArgsConstructor
public class ErdController {

    private final ErdService erdService;
    private final ReactFlowConverter reactFlowConverter;

    // ERD 사용자가 추가(생성)
    @PostMapping("{workspaceId}/erd")
    public ResponseEntity<SuccessResponse<?>> createErd(@AuthenticationPrincipal Users user,
                                                        @PathVariable("workspaceId") Long workspaceId) {
        Erd createdErd = erdService.createErd(user, workspaceId);

        Map<String, Object> data = new HashMap<>();
        data.put("erdId", createdErd.getErdId());
        data.put("createdAt", createdErd.getCreatedAt());
        data.put("workspaceId", createdErd.getWorkspaceId());
        data.put("tables", createdErd.getTables());

        SuccessResponse<?> response = new SuccessResponse<>("success", "ERD가 생성되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ERD AI 추천 요청
    @PostMapping("{workspaceId}/erds/recommendations")
    public ResponseEntity<SuccessResponse<?>> recommendErd(@AuthenticationPrincipal Users user,
                                                           @PathVariable("workspaceId")Long workspaceId) {
        log.info("== ERD AI 추천 요청 API 진입 ==");

        List<ErdAiCreateResponse> data = erdService.recommendErd(user, workspaceId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "AI로부터 ERD 추천을 성공적으로 받았습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ERD 조회하기
    @GetMapping("{workspaceId}/erd/{erdId}/flow")
    public ResponseEntity<SuccessResponse<?>> getFlow(@AuthenticationPrincipal Users user,
                                                      @PathVariable("workspaceId") Long workspaceId,
                                                      @PathVariable("erdId") Long erdId) {
        Erd erd = erdService.findByIdOrThrow(erdId);
        ERDFlowResponseDto dto = reactFlowConverter.convertToFlowResponse(erd);

        SuccessResponse<?> response = new SuccessResponse<>("success", "ERD 데이터 조회에 성공했습니다.", dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // workspaceId로 erdId 찾기
    @GetMapping("{workspaceId}/erd")
    public ResponseEntity<SuccessResponse<?>> getErdId(@AuthenticationPrincipal Users user,
                                                       @PathVariable("workspaceId") Long workspaceId) {

        Long data = erdService.findErdId(user, workspaceId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "ERDID를 반환하였습니다", Map.of("erdId", data));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
