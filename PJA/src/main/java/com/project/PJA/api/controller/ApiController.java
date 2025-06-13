package com.project.PJA.api.controller;

import com.project.PJA.api.dto.ApiRequest;
import com.project.PJA.api.dto.ApiResponse;
import com.project.PJA.api.service.ApiService;
import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class ApiController {
    private final ApiService apiService;

    // api 명세서 조회
    @GetMapping("/{workspaceId}/apis")
    public ResponseEntity<SuccessResponse<List<ApiResponse>>> getApi(@AuthenticationPrincipal Users user,
                                                                     @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        List<ApiResponse> apiResponses = apiService.getApi(userId, workspaceId);

        SuccessResponse<List<ApiResponse>> response = new SuccessResponse<>(
                "success", "API 명세서를 성공적으로 조회했습니다.", apiResponses
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // api 명세서 ai 요청
    
    // api 명세서 저장
    @PostMapping("/{workspaceId}/apis/confirm")
    public ResponseEntity<SuccessResponse<List<ApiResponse>>> saveApi(@AuthenticationPrincipal Users user,
                                                                      @PathVariable Long workspaceId,
                                                                      @RequestBody List<ApiRequest> apiRequests) {
        Long userId = user.getUserId();
        List<ApiResponse> apiResponses = apiService.saveApi(userId, workspaceId, apiRequests);

        SuccessResponse<List<ApiResponse>> response = new SuccessResponse<>(
                "success", "API 명세서를 성공적으로 저장했습니다.", apiResponses
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // api 생성
    @PostMapping("/{workspaceId}/apis")
    public ResponseEntity<SuccessResponse<ApiResponse>> createApi(@AuthenticationPrincipal Users user,
                                                                  @PathVariable Long workspaceId,
                                                                  @RequestBody ApiRequest apiRequest) {
        Long userId = user.getUserId();
        ApiResponse apiResponse = apiService.createApi(userId, workspaceId, apiRequest);

        SuccessResponse<ApiResponse> response = new SuccessResponse<>(
                "success", "API 명세서를 성공적으로 저장했습니다.", apiResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // api 수정
    @PutMapping("/{workspaceId}/apis/{apiId}")
    public ResponseEntity<SuccessResponse<ApiResponse>> updateApi(@AuthenticationPrincipal Users user,
                                                                  @PathVariable Long workspaceId,
                                                                  @PathVariable Long apiId,
                                                                  @RequestBody ApiRequest apiRequest) {
        Long userId = user.getUserId();
        ApiResponse apiResponse = apiService.updateApi(userId, workspaceId, apiId, apiRequest);

        SuccessResponse<ApiResponse> response = new SuccessResponse<>(
                "success", "API를 성공적으로 수정했습니다.", apiResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // api 삭제
    @DeleteMapping("/{workspaceId}/apis/{apiId}")
    public ResponseEntity<SuccessResponse<ApiResponse>> deleteApi(@AuthenticationPrincipal Users user,
                                                                  @PathVariable Long workspaceId,
                                                                  @PathVariable Long apiId) {
        Long userId = user.getUserId();
        ApiResponse apiResponse = apiService.deleteApi(userId, workspaceId, apiId);

        SuccessResponse<ApiResponse> response = new SuccessResponse<>(
                "success", "API 성공적으로 삭제했습니다.", apiResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
