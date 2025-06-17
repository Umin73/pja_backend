package com.project.PJA.api.controller;

import com.project.PJA.api.dto.ApiRequest;
import com.project.PJA.api.dto.ApiResponse;
import com.project.PJA.api.service.ApiService;
import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class ApiController {
    private final ApiService apiService;

    // API 명세서 조회
    @GetMapping("/{workspaceId}/apis")
    public ResponseEntity<SuccessResponse<List<ApiResponse>>> getApi(@AuthenticationPrincipal Users user,
                                                                     @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        log.info("=== API 명세서 조회 API 진입 == userId: {}", userId);
        
        List<ApiResponse> apiResponses = apiService.getApi(userId, workspaceId);

        SuccessResponse<List<ApiResponse>> response = new SuccessResponse<>(
                "success", "API 명세서를 성공적으로 조회했습니다.", apiResponses
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // API 명세서 AI 생성 요청
    @PostMapping("/{workspaceId}/apis/generate")
    public ResponseEntity<SuccessResponse<List<ApiResponse>>> generateApiSpecByAI(@AuthenticationPrincipal Users user,
                                                                                  @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        log.info("=== API 명세서 AI 생성 요청 API 진입 == userId: {}", userId);

        List<ApiResponse> apiResponses = apiService.generateApiSpecByAI(userId, workspaceId);

        SuccessResponse<List<ApiResponse>> response = new SuccessResponse<>(
                "success", "API 명세서를 성공적으로 생성했습니다.", apiResponses
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // API 생성
    @PostMapping("/{workspaceId}/apis")
    public ResponseEntity<SuccessResponse<ApiResponse>> createApi(@AuthenticationPrincipal Users user,
                                                                  @PathVariable Long workspaceId,
                                                                  @RequestBody ApiRequest apiRequest) {
        Long userId = user.getUserId();
        log.info("=== API 생성 API 진입 == userId: {}", userId);
        
        ApiResponse apiResponse = apiService.createApi(userId, workspaceId, apiRequest);

        SuccessResponse<ApiResponse> response = new SuccessResponse<>(
                "success", "API를 성공적으로 생성했습니다.", apiResponse
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // API 수정
    @PutMapping("/{workspaceId}/apis/{apiId}")
    public ResponseEntity<SuccessResponse<ApiResponse>> updateApi(@AuthenticationPrincipal Users user,
                                                                  @PathVariable Long workspaceId,
                                                                  @PathVariable Long apiId,
                                                                  @RequestBody ApiRequest apiRequest) {
        Long userId = user.getUserId();
        log.info("=== API 수정 API 진입 == userId: {}", userId);
        
        ApiResponse apiResponse = apiService.updateApi(userId, workspaceId, apiId, apiRequest);

        SuccessResponse<ApiResponse> response = new SuccessResponse<>(
                "success", "API를 성공적으로 수정했습니다.", apiResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // API 삭제
    @DeleteMapping("/{workspaceId}/apis/{apiId}")
    public ResponseEntity<SuccessResponse<ApiResponse>> deleteApi(@AuthenticationPrincipal Users user,
                                                                  @PathVariable Long workspaceId,
                                                                  @PathVariable Long apiId) {
        Long userId = user.getUserId();
        log.info("=== API 삭제 API 진입 == userId: {}", userId);
        
        ApiResponse apiResponse = apiService.deleteApi(userId, workspaceId, apiId);

        SuccessResponse<ApiResponse> response = new SuccessResponse<>(
                "success", "API를 성공적으로 삭제했습니다.", apiResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
