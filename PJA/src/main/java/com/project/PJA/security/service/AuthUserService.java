package com.project.PJA.security.service;

import com.project.PJA.exception.UnauthorizedException;
import com.project.PJA.security.dto.LoginDto;
import com.project.PJA.security.jwt.JwtTokenProvider;
import com.project.PJA.user.entity.UserStatus;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
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
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

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

        UserDetails user = userDetailsService.loadUserByUsername(uid);

        Long userId = userRepository.findUserIdByUid(uid);
        String accessToken = jwtTokenProvider.createToken(uid, user.getAuthorities().iterator().next().getAuthority(), userId);
        String refreshToken = jwtTokenProvider.createToken(uid, "REFRESH", userId);

        log.info("access token: {}", accessToken);

        redisTemplate.opsForValue().set("RT:" + uid, refreshToken, 30, TimeUnit.DAYS);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    public String reissue(HttpServletRequest request) {

        String rt = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new UnauthorizedException("Refresh Token 쿠키가 없습니다."));
        log.info("refresh token: {}", rt);
        
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

        Long userId = userRepository.findUserIdByUid(uid);
        UserDetails user = userDetailsService.loadUserByUsername(uid);
        log.info("user: {}", user);
        String newAccessToken = jwtTokenProvider.createToken(uid, user.getAuthorities().iterator().next().getAuthority(), userId);
        log.info("newAccessToken: {}", newAccessToken);

        return newAccessToken;
    }

    public boolean logout(HttpServletRequest request, String uid) {
        log.info("request: {}",request);
        String AT = jwtTokenProvider.resolveToken(request);

        if (AT == null) {
            throw new UnauthorizedException("유효한 Access Token이 전달되지 않았습니다.");
        }

        log.info("AT: {}",AT);
        long expiration = jwtTokenProvider.getExpiration(AT);

        // Redis에 블랙리스트로 등록
        redisTemplate.opsForValue().set("BL:" + AT, "logout", expiration, TimeUnit.MILLISECONDS);

        // Refresh Token 삭제
        redisTemplate.delete("RT:" + uid);

        return true;
    }
}
