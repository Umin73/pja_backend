package com.project.PJA.user.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.common.file.FileStorageService;
import com.project.PJA.user.dto.ChangePwRequestDto;
import com.project.PJA.user.dto.ChangeNameRequestDto;
import com.project.PJA.user.dto.UserProfileDto;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth/user") // 나중에 auth는 빼야함
@RequiredArgsConstructor
public class UserSettingController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final FileStorageService fileStoratgeService;

    @GetMapping("/read-info")
    public SuccessResponse<?> setting(@AuthenticationPrincipal Users user) {
        log.info("== 회원 정보 읽기 API 진입 == username: {}", user.getUsername());

        Map<String, Object> data = userService.getUserInfo(user);

        return new SuccessResponse<>("success", "계정 설정 정보를 성공적으로 조회했습니다.", data);
    }

    @PatchMapping("/change-name")
    public SuccessResponse<?> changeName(@AuthenticationPrincipal Users user,
                                         @RequestBody ChangeNameRequestDto dto) {
        log.info("== 회원 이름 변경 API 진입 == uid: {}", user.getUid());
        userService.updateUserName(user, dto.getNewName());
        return new SuccessResponse<>("success", "이름이 성공적으로 변경되었습니다.", null);
    }

    @PatchMapping("/change-pw")
    public SuccessResponse<?> changePassword(@AuthenticationPrincipal Users user,
                                             @RequestBody ChangePwRequestDto dto) {
        log.info("== 비밀번호 변경 API 진입 == uid: {}", user.getUid());
        userService.changePassword(user, dto);
        return new SuccessResponse<>("success", "비밀번호 변경에 성공하였습니다.", null);
    }

    @PostMapping("/profile-image")
    public SuccessResponse<?> uploadProfileImage(@AuthenticationPrincipal Users user,
                                                 @RequestParam("file")MultipartFile file) throws IOException {
        log.info("== 프로필 이미지 업데이트 API 진입 == uid: {}", user.getUid());
        userService.updateProfileImage(user, file);
        return new SuccessResponse<>("success", "프로필 이미지 업로드를 완료했습니다.", null);
    }

    @DeleteMapping("/profile-image")
    public SuccessResponse<?> deleteProfileImage(@AuthenticationPrincipal Users user) throws IOException{
        log.info("== 프로필 이미지 삭제 API 진입 == uid: {}", user.getUid());
        userService.deleteProfileImage(user);
        return new SuccessResponse<>("success", "프로필 이미지 삭제를 완료했습니다.", null);
    }
}
