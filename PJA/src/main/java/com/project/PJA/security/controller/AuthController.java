package com.project.PJA.security.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.exception.UnauthorizedException;
import com.project.PJA.security.dto.CheckPwDto;
import com.project.PJA.security.dto.LoginDto;
import com.project.PJA.security.dto.TokenRequestDto;
import com.project.PJA.security.service.AuthUserService;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserService authUserService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<?>> login(@RequestBody LoginDto loginDto) {
        log.info("== 로그인 API 진입 == UID: {}", loginDto.getUid());
        Map<Object, Object> tokens = authUserService.login(loginDto);

        SuccessResponse<?> response = new SuccessResponse<>("success", "로그인에 성공하였습니다", tokens);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/reissue")
    public ResponseEntity<SuccessResponse<?>> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        log.info("== 토큰 재발급 API 진입 == RT: {}", tokenRequestDto.getRefreshToken());
        String newAccessToken = authUserService.reissue(tokenRequestDto.getRefreshToken());

        SuccessResponse<?> response = new SuccessResponse<>("success", "토큰 재발급에 성공하였습니다.", Map.of("accessToken", newAccessToken));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/check-password")
    public ResponseEntity<SuccessResponse<?>> checkPassword(@AuthenticationPrincipal Users user,
                                            @RequestBody CheckPwDto checkPwDto) {
        if(!passwordEncoder.matches(checkPwDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("비밀번호가 올바르지 않습니다.");
        }

        SuccessResponse<?> response = new SuccessResponse<>("success", "비밀번호 확인에 성공하였습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<?>> logout(HttpServletRequest request, @AuthenticationPrincipal Users user) {
        log.info("== 로그아웃 API 진입 == uid: {}", user.getUid());
        if(authUserService.logout(request, user.getUid())) {
            SuccessResponse<?> response = new SuccessResponse<>("success", "로그아웃에 성공하였습니다.", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            throw new RuntimeException("로그아웃에 실패하였습니다.");
        }
    }
}