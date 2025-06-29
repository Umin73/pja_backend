package com.project.PJA.sse.controller;

import com.project.PJA.exception.NotFoundException;
import com.project.PJA.security.jwt.JwtTokenProvider;
import com.project.PJA.sse.service.SendEmitterService;
import com.project.PJA.sse.repository.SseEmitterRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class SseController {

    private final SseEmitterRepository sseEmitterRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final SendEmitterService sendEmitter;

    @GetMapping("/{workspaceId}/noti/subscribe")
    public SseEmitter subscribe(@PathVariable("workspaceId") Long workspaceId,
                                @RequestParam("token") String token) {

        log.info("== SSE 구독 API 진입 ==");

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        log.info("token은 {}", token);
        String uid = jwtTokenProvider.getUid(token);
        Long userId = userRepository.findByUid(uid)
                .map(Users::getUserId)
                .orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));
        
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 동안 SSE 연결

        sseEmitterRepository.save(workspaceId, userId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.delete(workspaceId, userId));
        emitter.onTimeout(() -> sseEmitterRepository.delete(workspaceId, userId));
        emitter.onError((e) -> sseEmitterRepository.delete(workspaceId,userId));

        // emitter 전송을 비동기 쓰레드에서
        // 이렇게 userId 값 꺼내오는거랑 emitter는 Async 쓰레드에서 전송하게 해야
        // DB 세션과 emitter이 얽히지 않음
        sendEmitter.sendEmitter(emitter);

        return emitter;
    }
}
