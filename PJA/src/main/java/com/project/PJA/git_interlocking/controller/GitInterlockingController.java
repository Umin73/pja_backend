package com.project.PJA.git_interlocking.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.git_interlocking.dto.GitInfoDto;
import com.project.PJA.git_interlocking.service.GitInterlockingService;
import com.project.PJA.user.entity.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workspaces")
public class GitInterlockingController {

    private final GitInterlockingService gitInterlockingService;

    public GitInterlockingController(GitInterlockingService gitInterlockingService) {
        this.gitInterlockingService = gitInterlockingService;
    }

    // Git 정보 생성
    @PostMapping("/{workspaceId}/git")
    ResponseEntity<SuccessResponse<?>> createGit(@AuthenticationPrincipal Users user,
                                                 @PathVariable Long workspaceId,
                                                 @RequestBody GitInfoDto dto) {

        String data = gitInterlockingService.createGit(user, workspaceId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "Git 정보 생성에 성공하였습니다.", Map.of("gitUrl",data));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Git 정보 조회
    @GetMapping("{workspaceId}/git")
    ResponseEntity<SuccessResponse<?>> getGitInfo(@AuthenticationPrincipal Users user,
                                                  @PathVariable Long workspaceId) {

        String data = gitInterlockingService.getGitUrl(user, workspaceId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "Git 정보 조회에 성공했습니다.", Map.of("gitUrl",data));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Git 정보 수정
    @PatchMapping("/{workspaceId}/git")
    ResponseEntity<SuccessResponse<?>> updateGit(@AuthenticationPrincipal Users user,
                                                 @PathVariable Long workspaceId,
                                                 @RequestBody GitInfoDto dto) {
        String data = gitInterlockingService.updateGitInfo(user, workspaceId, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "Git 정보 수정에 성공하였습니다.", Map.of("gitUrl",data));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
