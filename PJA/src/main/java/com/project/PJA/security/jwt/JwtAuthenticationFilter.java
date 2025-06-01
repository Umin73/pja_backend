package com.project.PJA.security.jwt;

import com.project.PJA.security.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final CustomUserDetailService customUserDetailService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, CustomUserDetailService customUserDetailService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.customUserDetailService = customUserDetailService;
    }

    // 인증 예외로 허용할 경로들
    private static final List<String> WHITELIST = List.of(
            "/",                       // 루트 경로
            "/api/auth/password",
            "/api/auth/user/request-reset",
            "/api/auth/user/email",
            "/api/auth/user/id",
            "/api/auth/user/change-pw",
            "/api/auth/login",
            "/api/auth/signup",
            "/api/auth/reissue",
            "/login/oauth2",          // 기본 OAuth2 URI
            "/login/oauth2/",
            "/login/oauth2/code/google",
            "/oauth2",                // 커스텀 OAuth2 URI
            "/oauth2/",
            "/oauth2/authorization/google"
    );

    private boolean isWhitelisted(String path) {
        return WHITELIST.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("== JwtAuthenticationFilter 진입: {} ==", path);

        // 인증 예외 경로는 필터 통과
        if(isWhitelisted(path)){
            log.info("허용된 경로 - 필터 통과: {}", path);
            filterChain.doFilter(request,response);
            return;
        }

        // Authorization 헤더에서 JWT 추출
        String token = jwtTokenProvider.resolveToken(request); // 토큰 추출
        log.info("추출된 토큰: {}", token);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String uid = jwtTokenProvider.getUid(token); // 토큰에서 사용자 식별(uid)를 추출
//            UserDetails userDetails = userDetailsService.loadUserByUsername(uid); // db에서 사용자 정보 조회
            UserDetails userDetails = customUserDetailService.loadUserByUsername(uid);
            log.info("UserDetails 로딩 성공: {}", userDetails.getUsername());

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // 인증된 사용자임을 설정 ( @AuthenticationPrincipal로 이 정보 꺼내 사용 )
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("인증 객체 설정 완료: {}", auth);
        }

        filterChain.doFilter(request, response);
    }
}
