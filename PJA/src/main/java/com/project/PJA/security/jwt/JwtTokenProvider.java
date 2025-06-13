package com.project.PJA.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    @Value("${JWT_SECRET}")
    private String secret;

    private Key key;

    private final long accessTokenValidity = 1000L * 60 * 1; // 1분
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 30; // 30일

    @PostConstruct
    public void init() {
        System.out.println("== JWT_SECRET 사용됨 == " + secret); // 디버그용
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String uid, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + (role.equals("REFRESH")? refreshTokenValidity : accessTokenValidity));

        return Jwts.builder()
                .setSubject(uid)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("토큰 검증 실패 (원인): {}", e.getClass().getSimpleName());
            log.debug("토큰: {}", token);
            return false;
        }
    }

    public String getUid(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public long getExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            long now = System.currentTimeMillis();
            return expiration.getTime() - now; // 남은 만료 시간(ms)
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("만료 시간 추출 실패: {}", e.getMessage());
            return 0;
        }
    }


    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        log.info("Authorization 헤더: {}", bearer);
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}