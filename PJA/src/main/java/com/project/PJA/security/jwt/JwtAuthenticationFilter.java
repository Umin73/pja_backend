package com.project.PJA.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    // 인증 예외로 허용할 경로들
    private static final List<String> WHITELIST = List.of(
            "/",                       // 루트 경로
            "/api/auth",              // 로그인 등 인증 관련
            "/api/auth/",
            "/api/auth/password",
            "/api/auth/user/request-reset",
            "/api/auth/user/email",
            "/api/auth/user/id",
            "/api/auth/user/change-pw",
            "/api/auth/logout",
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
        return WHITELIST.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 인증 예외 경로는 필터 통과
        if(isWhitelisted(path)){
            filterChain.doFilter(request,response);
            return;
        }

        // Authorization 헤더에서 JWT 추출
        String token = jwtTokenProvider.resolveToken(request); // 토큰 추출

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String uid = jwtTokenProvider.getUid(token); // 토큰에서 사용자 식별(uid)를 추출
            UserDetails userDetails = userDetailsService.loadUserByUsername(uid); // db에서 사용자 정보 조회

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
