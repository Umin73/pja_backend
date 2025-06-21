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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
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
    public ResponseEntity<SuccessResponse<?>> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        log.info("== 로그인 API 진입 == UID: {}", loginDto.getUid());
        Map<Object, Object> tokens = authUserService.login(loginDto);
        String refreshToken = (String) tokens.get("refreshToken");

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true) // HTTPS 환경에서만
                .path("/")    // 모든 경로에 대해 유효
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict") // 또는 Lax
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        SuccessResponse<?> successResponse = new SuccessResponse<>("success", "로그인에 성공하였습니다", Map.of("accessToken", tokens.get("accessToken")));
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }


    @PostMapping("/reissue")
    public ResponseEntity<SuccessResponse<?>> reissue(HttpServletRequest request) {
        log.info("== 토큰 재발급 API 진입 ==");
        String newAccessToken = authUserService.reissue(request);

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
    public ResponseEntity<SuccessResponse<?>> logout(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal Users user) {
        log.info("== 로그아웃 API 진입 == uid: {}", user.getUid());
        if(authUserService.logout(request, user.getUid())) {
            ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .path("/")
                    .maxAge(0) // 즉시 만료
                    .sameSite("Strict")
                    .build();
            response.setHeader("Set-Cookie", deleteCookie.toString());

            SuccessResponse<?> successResponse = new SuccessResponse<>("success", "로그아웃에 성공하였습니다.", null);
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        } else {
            throw new RuntimeException("로그아웃에 실패하였습니다.");
        }
    }
}