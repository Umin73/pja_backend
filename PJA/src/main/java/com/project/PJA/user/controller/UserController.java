package com.project.PJA.user.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.user.dto.*;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.user.service.UserService;
import com.project.PJA.workspace.dto.WorkspaceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController { // 로그인 하지 않고 접근 가능한 경우의 User Controller

    private final UserService userService;
    private final UserRepository userRepository;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<?>> signup(@RequestBody SignupDto signupDto) {
        log.info("== 회원가입 API 진입 ==");
        boolean success =userService.signup(signupDto);

        if (success) {
            SuccessResponse<?> response = new SuccessResponse<>("success", "회원가입(폼 입력)에 성공하였습니다", null);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            throw new RuntimeException("회원가입에 실패하였습니다.");
        }
    }

    // 회원가입 인증코드 이메일 보내기
    @PostMapping("/send-email")
    public ResponseEntity<SuccessResponse<?>> sendVerifyEmail(@RequestBody SendVerifyEmailDto dto) {
        log.info("== 이메일 보내기 API 진입 == UID: {}", dto.getEmail());
        userService.sendVerificationEmail(dto.getEmail());

        SuccessResponse<?> response = new SuccessResponse<>("success", "이메일 인증이 완료되었습니다.", null);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 회원가입 인증코드 인증
    @PostMapping("/verify-email")
    public ResponseEntity<SuccessResponse<?>> verifyEmail(@RequestBody VerifyEmailDto dto) {
        log.info("== 회원가입 이메일 인증 API 진입 == token={}", dto.getToken());
        userService.verifyEmail(dto.getEmail(), dto.getToken());

        SuccessResponse<?> response = new SuccessResponse<>("success", "이메일 인증이 완료되었습니다.", null);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 아이디 찾기
    @PostMapping("/find-id")
    public ResponseEntity<SuccessResponse<?>> findId(@RequestBody EmailRequestDto emailRequestDto) {
        log.info("== 아이디 찾기 API 진입 == email: {}", emailRequestDto.getEmail());
        Map<String, String> data = userService.findId(emailRequestDto.getEmail());

        SuccessResponse<?> response = new SuccessResponse<>("success", "요청하신 아이디를 성공적으로 찾았습니다.", data);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 이메일 찾기
    @PostMapping("/find-email")
    public ResponseEntity<SuccessResponse<?>> findEmail(@RequestBody UidRequestDto uidRequestDto) {
        log.info("== 이메일 찾기 API 진입 == email: {}", uidRequestDto.getUid());
        Map<String, String> data = userService.findEmail(uidRequestDto.getUid());

        SuccessResponse<?> response = new SuccessResponse<>("success", "요청하신 이메일을 성공적으로 찾았습니다.", data);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 비밀번호 찾기(비밀번호 찾기 인증코드 이메일 보내기)
    @PostMapping("/find-pw")
    public ResponseEntity<SuccessResponse<?>> findPw(@RequestBody IdEmailRequestDto idEmailRequestDto) {
        log.info("== 비밀번호 찾기 API 진입 == email: {}", idEmailRequestDto.getUid());
        userService.sendFindPwEmail(idEmailRequestDto);

        SuccessResponse<?> response = new SuccessResponse<>("success","인증번호를 성공적으로 발송했습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 비밀번호 찾기 인증번호 인증
    @PostMapping("/verify-pw-code")
    public ResponseEntity<SuccessResponse<?>> verifyPwCode(@RequestBody VerifyEmailRequestDto verifyEmailRequestDto) {
        log.info("== 비밀번호 찾기 인증번호 확인 API 진입 == token: {}", verifyEmailRequestDto.getToken());
        userService.verifyFindPwCode(verifyEmailRequestDto);

         SuccessResponse<?> response = new SuccessResponse<>("success", "인증이 완료되었습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 새 비밀번호로 변경
    @PatchMapping("/change-pw")
    public ResponseEntity<SuccessResponse<?>> changePassword(@RequestBody ChangePw2RequestDto changePw2RequestDto) {
        log.info("== 비밀번호 변경 API 진입 == uid: {}", changePw2RequestDto.getUid());
        userService.changePassword2(changePw2RequestDto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "비밀번호 변경에 성공하였습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 아이디 중복 확인
    @GetMapping("/check-uid")
    public ResponseEntity<SuccessResponse<?>> checkUid(@RequestParam String uid) {
        boolean isDuplicated = userRepository.existsByUid(uid);

        SuccessResponse<?> response = new SuccessResponse<>("success", "아이디 중복 확인이 완료되었습니다.", Map.of("isDuplicated", isDuplicated));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<SuccessResponse<?>> checkEmail(@RequestParam String email) {
        boolean isDuplicated = userRepository.existsByEmail(email);

        SuccessResponse<?> response = new SuccessResponse<>("success", "이메일 중복 확인이 완료되었습니다.", Map.of("isDuplicated", isDuplicated));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
