package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.project_progress.dto.CreateProgressDto;
import com.project.PJA.project_progress.dto.UpdateProgressDto;
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
@RequestMapping("/api/workspace/")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureService featureService;

    @PostMapping("{workspaceId}/project/category/{categoryId}/feature")
    ResponseEntity<SuccessResponse<?>> createFeature(@AuthenticationPrincipal Users user,
                                                      @PathVariable("workspaceId") Long workspaceId,
                                                      @PathVariable("categoryId") Long categoryId,
                                                      @RequestBody CreateProgressDto dto) {
        log.info("== 기능 생성 API 진입, {} ==", dto);
        Long data = featureService.createFeature(user, workspaceId, categoryId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "기능이 생성되었습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}")
    public ResponseEntity<SuccessResponse<?>> updateAction(@AuthenticationPrincipal Users user,
                                                           @PathVariable("workspaceId") Long workspaceId,
                                                           @PathVariable("categoryId") Long categoryId,
                                                           @PathVariable("featureId") Long featureId,
                                                           @RequestBody UpdateProgressDto dto) {
        featureService.updateFeature(user, workspaceId, categoryId, featureId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "기능이 수정되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @PatchMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/name")
//    ResponseEntity<SuccessResponse<?>> updateName(@AuthenticationPrincipal Users user,
//                                                  @PathVariable("workspaceId") Long workspaceId,
//                                                  @PathVariable("featureId") Long featureId,
//                                                  @RequestBody UpdateProgressDto dto) {
//        log.info("== 기능 이름 수정 API 진입, {} ==", dto);
//        featureService.updateName(user, workspaceId, featureId, dto.getName());
//        SuccessResponse<?> response = new SuccessResponse<>("success", "기능의 이름이 수정되었습니다.", null);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PatchMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/start-date")
//    ResponseEntity<SuccessResponse<?>> updateStartDate(@AuthenticationPrincipal Users user,
//                                                       @PathVariable("workspaceId") Long workspaceId,
//                                                       @PathVariable("featureId") Long featureId,
//                                                       @RequestBody UpdateProgressDto dto) {
//        featureService.updateStartDate(user, workspaceId, featureId, dto.getStartDate());
//        SuccessResponse<?> response = new SuccessResponse<>("success", "기능의 시작일이 수정되었습니다.", null);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PatchMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/end-date")
//    ResponseEntity<SuccessResponse<?>> updateEndDate(@AuthenticationPrincipal Users user,
//                                                     @PathVariable("workspaceId") Long workspaceId,
//                                                     @PathVariable("featureId") Long featureId,
//                                                     @RequestBody UpdateProgressDto dto) {
//        featureService.updateEndDate(user, workspaceId, featureId, dto.getEndDate());
//        SuccessResponse<?> response = new SuccessResponse<>("success", "기능의 마감일이 수정되었습니다.", null);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PatchMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/state")
//    ResponseEntity<SuccessResponse<?>> updateState(@AuthenticationPrincipal Users user,
//                                                   @PathVariable("workspaceId") Long workspaceId,
//                                                   @PathVariable("featureId") Long featureId,
//                                                   @RequestBody UpdateProgressDto dto) {
//        featureService.updateState(user, workspaceId, featureId, dto.getState());
//        SuccessResponse<?> response = new SuccessResponse<>("success", "기능의 상태가 수정되었습니다.", null);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PatchMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/importance")
//    ResponseEntity<SuccessResponse<?>> updateImportance(@AuthenticationPrincipal Users user,
//                                                        @PathVariable("workspaceId") Long workspaceId,
//                                                        @PathVariable("featureId") Long featureId,
//                                                        @RequestBody UpdateProgressDto dto) {
//        featureService.updateImportance(user, workspaceId, featureId, dto.getImportance());
//        SuccessResponse<?> response = new SuccessResponse<>("success", "기능의 중요도가 수정되었습니다.", null);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PatchMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/order")
//    ResponseEntity<SuccessResponse<?>> updateOrder(@AuthenticationPrincipal Users user,
//                                                   @PathVariable("workspaceId") Long workspaceId,
//                                                   @PathVariable("featureId") Long featureId,
//                                                   @RequestBody UpdateProgressDto dto) {
//        featureService.updateOrderIndex(user, workspaceId, featureId, dto.getOrderIndex());
//        SuccessResponse<?> response = new SuccessResponse<>("success", "기능의 순서가 수정되었습니다.", null);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PatchMapping("{workspaceId}/project/category/{categoryId}/feature/{featureId}/participants")
//    ResponseEntity<SuccessResponse<?>> updateParticipants(@AuthenticationPrincipal Users user,
//                                                          @PathVariable("workspaceId") Long workspaceId,
//                                                          @PathVariable("featureId") Long featureId,
//                                                          @RequestBody UpdateProgressDto dto) {
//        featureService.updateParticipants(user, workspaceId, featureId, dto.getParticipantIds());
//        SuccessResponse<?> response = new SuccessResponse<>("success", "기능의 참여자가 수정되었습니다.", null);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

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
