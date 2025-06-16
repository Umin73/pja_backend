package com.project.PJA.project_progress.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.project_progress.dto.ProjectProgressResponseDto;
import com.project.PJA.project_progress.service.ProjectProgressService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/workspaces/")
@RequiredArgsConstructor
public class ProjectProgressController {

    private final ProjectProgressService projectProgressService;

    @GetMapping("{workspaceId}/project/progress")
    ResponseEntity<SuccessResponse<?>> readProjectProgress(@AuthenticationPrincipal Users user,
                                                           @PathVariable("workspaceId") Long workspaceId) {
        log.info("== 프로젝트 진행 조회 API ==");
        ProjectProgressResponseDto dto = projectProgressService.getProjectProcessInfo(user.getUserId(), workspaceId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "프로젝트 진행 내용이 조회되었습니다.", dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
