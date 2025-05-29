package com.project.PJA.security.controller;

import com.project.PJA.common.dto.ErrorResponse;
import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.security.dto.LoginDto;
import com.project.PJA.security.dto.TokenRequestDto;
import com.project.PJA.security.jwt.JwtTokenProvider;
import com.project.PJA.security.service.AuthUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserService authUserService;

    @PostMapping("/login")
    public SuccessResponse<?> login(@RequestBody LoginDto loginDto) {
        log.info("== 로그인 API 진입 == UID: {}", loginDto.getUid());
        Map<Object, Object> tokens = authUserService.login(loginDto);
        return new SuccessResponse<>("success", "로그인에 성공하였습니다", tokens);
    }


    @PostMapping("/reissue")
    public SuccessResponse<?> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        log.info("== 토큰 재발급 API 진입 == RT: {}", tokenRequestDto.getRefreshToken());
        String newAccessToken = authUserService.reissue(tokenRequestDto.getRefreshToken());
        return new SuccessResponse<>("success", "토큰 재발급에 성공하였습니다.", Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public SuccessResponse<?> logout(HttpServletRequest request, @RequestBody String uid) {
        if(authUserService.logout(request, uid)) {
            return new SuccessResponse<>("success", "로그아웃에 성공하였습니다.", null);
        } else {
            throw new RuntimeException("로그아웃에 실패하였습니다.");
        }
    }
}