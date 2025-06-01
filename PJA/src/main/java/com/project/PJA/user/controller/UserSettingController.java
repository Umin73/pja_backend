package com.project.PJA.user.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.user.dto.ChangeNameRequestDto;
import com.project.PJA.user.dto.UidRequestDto;
import com.project.PJA.user.dto.UserProfileDto;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth/user") // 나중에 auth는 빼야함
@RequiredArgsConstructor
public class UserSettingController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/read-info")
    public SuccessResponse<?> setting(/*@AuthenticationPrincipal Users user*/ @RequestParam String uid) {
        log.info("== 회원 정보 읽기 API 진입 == uid: {}", uid);

        // Optional로 받아오는거 추후 삭제 필요
        Optional<Users> optionalUsers = userRepository.findByUid(uid);
        if(optionalUsers.isEmpty()) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        Users user = optionalUsers.get();
        UserProfileDto dto = new UserProfileDto(user.getName(), user.getProfileImage());
        return new SuccessResponse<>("success", "계정 설정 정보를 성공적으로 조회했습니다.", dto);
    }

    @PatchMapping("/change-name")
    public SuccessResponse<?> changeName(@AuthenticationPrincipal Users user,
                                         @RequestBody ChangeNameRequestDto dto) {
        log.info("== 회원 이름 변경 API 진입 == uid: {}", user.getUid());
        userService.updateUserName(user.getUid(), dto.getNewName());
        return new SuccessResponse<>("success", "이름이 성공적으로 변경되었습니다.", null);
    }

    @PatchMapping("/change-name-test")
    public SuccessResponse<?> changeNameTest(@RequestParam String uid,
                                             @RequestBody ChangeNameRequestDto dto) {
        log.info("== [임시 테스트] 회원 이름 변경 API 진입 == uid: {}", uid);
        userService.updateUserName(uid, dto.getNewName());
        return new SuccessResponse<>("success", "이름이 성공적으로 변경되었습니다.", null);
    }

}
