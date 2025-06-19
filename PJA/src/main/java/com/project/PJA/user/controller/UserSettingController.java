package com.project.PJA.user.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.common.file.FileStorageService;
import com.project.PJA.user.dto.*;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/user") // 나중에 auth는 빼야함
@RequiredArgsConstructor
public class UserSettingController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final FileStorageService fileStoratgeService;

    // 회원 정보(프로필 이미지, 이름) 불러오기
    @GetMapping("/read-info")
    public ResponseEntity<SuccessResponse<?>> setting(@AuthenticationPrincipal Users user) {
        log.info("== 회원 정보 읽기 API 진입 == username: {}", user.getUsername());

        Map<String, Object> data = userService.getUserInfo(user);

        SuccessResponse<?> response = new SuccessResponse<>("success", "계정 설정 정보를 성공적으로 조회했습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<SuccessResponse<?>> delete(@AuthenticationPrincipal Users user) {
        log.info("== 회원 탈퇴 API 진입 == uid: {}", user.getUserId());
        userService.withdraw(user);

        SuccessResponse<?> response = new SuccessResponse<>("success", "회원 탈퇴가 성공적으로 처리되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 회원 이름 변경
    @PatchMapping("/change-name")
    public ResponseEntity<SuccessResponse<?>> changeName(@AuthenticationPrincipal Users user,
                                         @RequestBody ChangeNameRequestDto dto) {
        log.info("== 회원 이름 변경 API 진입 == uid: {}", user.getUid());
        String newName = userService.updateUserName(user, dto.getNewName());

        SuccessResponse<?> response = new SuccessResponse<>("success", "이름이 성공적으로 변경되었습니다.", Map.of("newName",newName));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 비밀번호 변경
    @PatchMapping("/change-pw")
    public ResponseEntity<SuccessResponse<?>> changePassword(@AuthenticationPrincipal Users user,
                                             @RequestBody ChangePwRequestDto dto) {
        log.info("== 비밀번호 변경 API 진입 == uid: {}", user.getUid());
        userService.changePassword(user, dto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "비밀번호 변경에 성공하였습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 프로필 이미지 업데이트
    @PostMapping("/profile-image")
    public ResponseEntity<SuccessResponse<?>> uploadProfileImage(@AuthenticationPrincipal Users user,
                                                 @RequestParam("file")MultipartFile file) throws IOException {
        log.info("== 프로필 이미지 업데이트 API 진입 == uid: {}", user.getUid());
        userService.updateProfileImage(user, file);

        SuccessResponse<?> response = new SuccessResponse<>("success", "프로필 이미지 업로드를 완료했습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/profile-image")
    public ResponseEntity<SuccessResponse<?>> deleteProfileImage(@AuthenticationPrincipal Users user) throws IOException{
        log.info("== 프로필 이미지 삭제 API 진입 == uid: {}", user.getUid());
        userService.deleteProfileImage(user);

        SuccessResponse<?> response = new SuccessResponse<>("success", "프로필 이미지 삭제를 완료했습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
