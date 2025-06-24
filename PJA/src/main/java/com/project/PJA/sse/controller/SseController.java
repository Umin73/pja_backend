package com.project.PJA.sse.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.security.jwt.JwtTokenProvider;
import com.project.PJA.sse.repository.SseEmitterRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.http.HttpResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class SseController {

    private final SseEmitterRepository sseEmitterRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @GetMapping("/{workspaceId}/noti/subscribe")
    public SseEmitter subscribe(@PathVariable("workspaceId") Long workspaceId,
                                @RequestParam("token") String token) {

        log.info("== SSE 구독 API 진입 ==");

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        log.info("token은 {}", token);
        String uid = jwtTokenProvider.getUid(token);
        Users user = userRepository.findByUid(uid)
                .orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));
        Long userId = user.getUserId();
        
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 동안 SSE 연결

        sseEmitterRepository.save(workspaceId, userId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.delete(workspaceId, userId));
        emitter.onTimeout(() -> sseEmitterRepository.delete(workspaceId, userId));
        emitter.onError((e) -> sseEmitterRepository.delete(workspaceId,userId));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 성공")
                    .reconnectTime(10_000L));
        } catch (Exception e) {
            throw new RuntimeException("SSE 연결 오류가 발생했습니다.", e);
        }

        return emitter;
    }
}
