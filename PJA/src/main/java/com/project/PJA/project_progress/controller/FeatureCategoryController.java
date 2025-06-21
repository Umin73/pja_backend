package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.project_progress.dto.CreateActionDto;
import com.project.PJA.project_progress.dto.CreateCategoryAndFeatureDto;
import com.project.PJA.project_progress.dto.UpdateFeatureAndCategoryDto;
import com.project.PJA.project_progress.service.FeatureCategoryService;
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
public class FeatureCategoryController {

    private final FeatureCategoryService featureCategoryService;

    @PostMapping("{workspaceId}/project/category")
    ResponseEntity<SuccessResponse<?>> createCategory(@AuthenticationPrincipal Users user,
                                                      @PathVariable("workspaceId") Long workspaceId,
                                                      @RequestBody CreateCategoryAndFeatureDto dto) {
        log.info("== 기능 카테고리 생성 API 진입, {} ==", dto);
        Long data = featureCategoryService.createFeatureCategory(user, workspaceId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "카테고리가 생성되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{workspaceId}/project/category/{categoryId}")
    public ResponseEntity<SuccessResponse<?>> updateAction(@AuthenticationPrincipal Users user,
                                                           @PathVariable("workspaceId") Long workspaceId,
                                                           @PathVariable("categoryId") Long categoryId,
                                                           @RequestBody UpdateFeatureAndCategoryDto dto) {
        featureCategoryService.updateCategory(user, workspaceId, categoryId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "카테고리가 수정되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{workspaceId}/project/category/{categoryId}")
    ResponseEntity<SuccessResponse<?>> deleteCategory(@AuthenticationPrincipal Users user,
                                                      @PathVariable("workspaceId") Long workspaceId,
                                                      @PathVariable("categoryId") Long categoryId) {
        featureCategoryService.deleteFeatureCategory(user, workspaceId, categoryId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "카테고리가 삭제되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
