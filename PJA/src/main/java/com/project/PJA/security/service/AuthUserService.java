package com.project.PJA.security.service;

import com.project.PJA.exception.UnauthorizedException;
import com.project.PJA.security.dto.LoginDto;
import com.project.PJA.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    public Map<Object, Object> login(LoginDto loginDto) {
        String uid = loginDto.getUid();
        String password = loginDto.getPassword();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(uid, password));
        } catch (BadCredentialsException e) {
            log.error("인증 실패 - 잘못된 자격 증명", e);
            throw new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다.");
        } catch (Exception e) {
            log.error("로그인 중 예외 발생", e); // <-- 추가
            throw e;
        }

        log.info("uid는 " + uid);
        log.info("password는 " + password);
        UserDetails user = userDetailsService.loadUserByUsername(uid);
        String accessToken = jwtTokenProvider.createToken(uid, user.getAuthorities().iterator().next().getAuthority());
        String refreshToken = jwtTokenProvider.createToken(uid, "REFRESH");

        redisTemplate.opsForValue().set("RT:" + uid, refreshToken, 7, TimeUnit.DAYS);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    public String reissue(String rt) {
        if(!jwtTokenProvider.validateToken(rt)) {
            log.info("RT 유효하지 않음 -- (1)");
            throw new UnauthorizedException("Refresh Token이 유효하지 않거나 만료되었습니다.");
        }

        String uid = jwtTokenProvider.getUid(rt);
        log.info("uid: {}", uid);
        String storedToken = redisTemplate.opsForValue().get("RT:"+uid);
        log.info("storedToken: {}", storedToken);

        if(storedToken == null || !storedToken.equals(rt)) {
            log.info("RT 유효하지 않음 -- (2)");
            throw new UnauthorizedException("Refresh Token이 유효하지 않거나 만료되었습니다.");
        }

        UserDetails user = userDetailsService.loadUserByUsername(uid);
        log.info("user: {}", user);
        String newAccessToken = jwtTokenProvider.createToken(uid, user.getAuthorities().iterator().next().getAuthority());

        return newAccessToken;
    }

    public boolean logout(HttpServletRequest request, String uid) {
        String AT = jwtTokenProvider.resolveToken(request);
        long expiration = jwtTokenProvider.getExpiration(AT);

        // Redis에 블랙리스트로 등록
        redisTemplate.opsForValue().set("BL:" + AT, "logout", expiration, TimeUnit.MILLISECONDS);

        // Refresh Token 삭제
        redisTemplate.delete("RT:" + uid);

        return true;
    }

}
