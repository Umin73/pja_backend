package com.project.PJA.security.controller;

import com.project.PJA.security.jwt.JwtTokenProvider;
import com.project.PJA.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String uid, @RequestParam String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(uid, password));

        UserDetails user = userDetailsService.loadUserByUsername(uid);
        String accessToken = jwtTokenProvider.createToken(uid, user.getAuthorities().iterator().next().getAuthority());
        String refreshToken = jwtTokenProvider.createToken(uid, "REFRESH");

        // Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set("RT:" + uid, refreshToken, 7, TimeUnit.DAYS);

        return ResponseEntity.ok().body(
                Map.of("status","success",
                        "message", "로그인에 성공하였습니다.",
                        "accessToken", accessToken,
                        "refreshToken", refreshToken)
        );
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