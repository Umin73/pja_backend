package com.project.PJA.user.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.user.dto.*;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public SuccessResponse<?> signup(@RequestBody SignupDto signupDto) {
        boolean success =userService.signup(signupDto);

        if (success) {
            return new SuccessResponse<>("success", "회원가입(폼 입력)에 성공하였습니다", null);
        } else {
            throw new RuntimeException("회원가입에 실패하였습니다.");
        }
    }

    @GetMapping("/send-email")
    public SuccessResponse<?> sendVerifyEmail(@RequestParam String email) {
        log.info("== 이메일 보내기 API 진입 == UID: {}", email);
        userService.sendVerificationEmail(email);
        return new SuccessResponse<>("success", "이메일 인증이 완료되었습니다.", null);
    }

    @GetMapping("/verify-email")
    public SuccessResponse<?> verifyEmail(@RequestParam String email, @RequestParam String token) {
        log.info("== 회원가입 이메일 인증 API 진입 == UID: {}, token={}", email, token);
        userService.verifyEmail(email, token);
        return new SuccessResponse<>("success", "이메일 인증이 완료되었습니다.", null);
    }

    @PostMapping("/find-id")
    public SuccessResponse<?> findId(@RequestBody EmailRequestDto emailRequestDto) {
        log.info("== 아이디 찾기 API 진입 == email: {}", emailRequestDto.getEmail());
        Map<String, String> data = userService.findId(emailRequestDto.getEmail());
        return new SuccessResponse<>("success", "요청하신 아이디를 성공적으로 찾았습니다.", data);
    }

    @PostMapping("/find-pw")
    public SuccessResponse<?> findPw(@RequestBody IdEmailRequestDto idEmailRequestDto) {
        log.info("== 비밀번호 찾기 API 진입 == email: {}", idEmailRequestDto.getUid());
        userService.sendFindPwEmail(idEmailRequestDto);
        return new SuccessResponse<>("success","인증번호를 성공적으로 발송했습니다.", null);
    }

    @PostMapping("/verify-pw-code")
    public SuccessResponse<?> verifyPwCode(@RequestBody VerifyEmailRequestDto verifyEmailRequestDto) {
        log.info("== 비밀번호 찾기 인증번호 확인 API 진입 == token: {}", verifyEmailRequestDto.getToken());
        userService.verifyFindPwCode(verifyEmailRequestDto);
        return new SuccessResponse<>("success", "인증이 완료되었습니다.", null);
    }

    @PostMapping("/find-email")
    public SuccessResponse<?> findEmail(@RequestBody UidRequestDto uidRequestDto) {
        log.info("== 이메일 찾기 API 진입 == email: {}", uidRequestDto.getUid());
        Map<String, String> data = userService.findEmail(uidRequestDto.getUid());
        return new SuccessResponse<>("success", "요청하신 이메일을 성공적으로 찾았습니다.", data);
    }

    @DeleteMapping("/delete")
    public SuccessResponse<?> delete(/*@AuthenticationPrincipal Users user*/ @RequestBody UidRequestDto uidRequestDto) {
        log.info("== 회원 탈퇴 API 진입 == uid: {}", uidRequestDto.getUid());
        userService.withdraw(uidRequestDto.getUid());
        return new SuccessResponse<>("success", "회원 탈퇴가 성공적으로 처리되었습니다.", null);
    }
}
