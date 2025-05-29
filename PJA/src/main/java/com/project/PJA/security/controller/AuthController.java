package com.project.PJA.security.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.security.dto.LoginDto;
import com.project.PJA.security.jwt.JwtTokenProvider;
import com.project.PJA.security.service.AuthUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final StringRedisTemplate redisTemplate;
    private final AuthUserService authUserService;

    @PostMapping("/login")
    public SuccessResponse<?> login(@RequestBody LoginDto loginDto) {
        log.info("== 로그인 API 진입 == UID: {}", loginDto.getUid());
        try {
            Map<Object, Object> tokens = authUserService.login(loginDto);
            return new SuccessResponse<>("success", "로그인에 성공하였습니다", tokens);
        } catch (Exception e) {
            log.error("로그인 중 예외 발생", e);
            return new SuccessResponse<>("fail", "로그인 중 오류 발생", null);
        }
    }


    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestParam String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) return ResponseEntity.status(401).build();

        String uid = jwtTokenProvider.getUid(refreshToken);
        String storedToken = redisTemplate.opsForValue().get("RT:" + uid);

        if (storedToken == null || !storedToken.equals(refreshToken)) return ResponseEntity.status(401).build();

        UserDetails user = userDetailsService.loadUserByUsername(uid);
        String newAccessToken = jwtTokenProvider.createToken(uid, user.getAuthorities().iterator().next().getAuthority());

        return ResponseEntity.ok().body(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String uid) {
        redisTemplate.delete("RT:" + uid);
        return ResponseEntity.ok().body(Map.of("message", "로그아웃에 성공하였습니다."));
    }
}