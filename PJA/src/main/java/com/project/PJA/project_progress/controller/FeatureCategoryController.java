package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
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
@RequestMapping("/api/workspace/")
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

    @PatchMapping("{workspaceId}/project/category/{categoryId}/name")
    ResponseEntity<SuccessResponse<?>> updateName(@AuthenticationPrincipal Users user,
                                                  @PathVariable("workspaceId") Long workspaceId,
                                                  @PathVariable("categoryId") Long categoryId,
                                                  @RequestBody UpdateFeatureAndCategoryDto dto) {
        featureCategoryService.updateName(user, workspaceId, categoryId, dto.getName());
        SuccessResponse<?> response = new SuccessResponse<>("success", "카테고리의 이름이 수정되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/project/category/{categoryId}/start-date")
    ResponseEntity<SuccessResponse<?>> updateStartDate(@AuthenticationPrincipal Users user,
                                                  @PathVariable("workspaceId") Long workspaceId,
                                                  @PathVariable("categoryId") Long categoryId,
                                                  @RequestBody UpdateFeatureAndCategoryDto dto) {
        featureCategoryService.updateStartDate(user, workspaceId, categoryId, dto.getStartDate());
        SuccessResponse<?> response = new SuccessResponse<>("success", "카테고리의 시작일이 수정되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/project/category/{categoryId}/end-date")
    ResponseEntity<SuccessResponse<?>> updateEndDate(@AuthenticationPrincipal Users user,
                                                       @PathVariable("workspaceId") Long workspaceId,
                                                       @PathVariable("categoryId") Long categoryId,
                                                       @RequestBody UpdateFeatureAndCategoryDto dto) {
        featureCategoryService.updateEndDate(user, workspaceId, categoryId, dto.getEndDate());
        SuccessResponse<?> response = new SuccessResponse<>("success", "카테고리의 마감일이 수정되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/project/category/{categoryId}/state")
    ResponseEntity<SuccessResponse<?>> updateState(@AuthenticationPrincipal Users user,
                                                     @PathVariable("workspaceId") Long workspaceId,
                                                     @PathVariable("categoryId") Long categoryId,
                                                     @RequestBody UpdateFeatureAndCategoryDto dto) {
        featureCategoryService.updateState(user, workspaceId, categoryId, dto.getState());
        SuccessResponse<?> response = new SuccessResponse<>("success", "카테고리의 상태가 수정되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/project/category/{categoryId}/importance")
    ResponseEntity<SuccessResponse<?>> updateImportance(@AuthenticationPrincipal Users user,
                                                   @PathVariable("workspaceId") Long workspaceId,
                                                   @PathVariable("categoryId") Long categoryId,
                                                   @RequestBody UpdateFeatureAndCategoryDto dto) {
        featureCategoryService.updateImportance(user, workspaceId, categoryId, dto.getImportance());
        SuccessResponse<?> response = new SuccessResponse<>("success", "카테고리의 중요도가 수정되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/project/category/{categoryId}/order")
    ResponseEntity<SuccessResponse<?>> updateOrder(@AuthenticationPrincipal Users user,
                                                        @PathVariable("workspaceId") Long workspaceId,
                                                        @PathVariable("categoryId") Long categoryId,
                                                        @RequestBody UpdateFeatureAndCategoryDto dto) {
        featureCategoryService.updateOrderIndex(user, workspaceId, categoryId, dto.getOrderIndex());
        SuccessResponse<?> response = new SuccessResponse<>("success", "카테고리의 순서가 수정되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{workspaceId}/project/category/{categoryId}/participants")
    ResponseEntity<SuccessResponse<?>> updateParticipants(@AuthenticationPrincipal Users user,
                                                          @PathVariable("workspaceId") Long workspaceId,
                                                          @PathVariable("categoryId") Long categoryId,
                                                          @RequestBody UpdateFeatureAndCategoryDto dto) {
        featureCategoryService.updateParticipants(user, workspaceId, categoryId, dto.getParticipantIds());
        SuccessResponse<?> response = new SuccessResponse<>("success", "카테고리의 참여자가 수정되었습니다.", null);
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
