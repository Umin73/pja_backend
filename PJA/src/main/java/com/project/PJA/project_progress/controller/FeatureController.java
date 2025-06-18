package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.project_progress.dto.CreateCategoryAndFeatureDto;
import com.project.PJA.project_progress.dto.UpdateActionDto;
import com.project.PJA.project_progress.dto.UpdateFeatureAndCategoryDto;
import com.project.PJA.project_progress.service.FeatureService;
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
public class FeatureController {

    private final FeatureService featureService;

    @PostMapping("{workspaceId}/project/category/{categoryId}/feature")
    ResponseEntity<SuccessResponse<?>> createFeature(@AuthenticationPrincipal Users user,
                                                      @PathVariable("workspaceId") Long workspaceId,
                                                      @PathVariable("categoryId") Long categoryId,
                                                      @RequestBody CreateCategoryAndFeatureDto dto) {
        log.info("== 기능 생성 API 진입, {} ==", dto);
        Long data = featureService.createFeature(user, workspaceId, categoryId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "기능이 생성되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}")
    public ResponseEntity<SuccessResponse<?>> updateFeature(@AuthenticationPrincipal Users user,
                                                            @PathVariable("workspaceId") Long workspaceId,
                                                            @PathVariable("categoryId") Long categoryId,
                                                            @PathVariable("featureId") Long featureId,
                                                            @RequestBody UpdateFeatureAndCategoryDto dto) {
        featureService.updateFeature(user, workspaceId, categoryId, featureId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "기능이 수정되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}")
    ResponseEntity<SuccessResponse<?>> deleteFeature(@AuthenticationPrincipal Users user,
                                                      @PathVariable("workspaceId") Long workspaceId,
                                                      @PathVariable("categoryId") Long categoryId,
                                                      @PathVariable("featureId") Long featureId) {
        featureService.deleteFeature(user, workspaceId, categoryId, featureId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "기능이 삭제되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
