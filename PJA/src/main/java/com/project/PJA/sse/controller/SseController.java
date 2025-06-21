package com.project.PJA.sse.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.sse.repository.SseEmitterRepository;
import com.project.PJA.user.entity.Users;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.http.HttpResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class SseController {

    private final SseEmitterRepository sseEmitterRepository;


    @GetMapping("/{workspaceId}/noti/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal Users user,
                                                     @PathVariable("workspaceId") Long workspaceId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 동안 SSE 연결

        sseEmitterRepository.save(workspaceId, user.getUserId(), emitter);

        emitter.onCompletion(() -> sseEmitterRepository.delete(workspaceId, user.getUserId()));
        emitter.onTimeout(() -> sseEmitterRepository.delete(workspaceId, user.getUserId()));
        emitter.onError((e) -> sseEmitterRepository.delete(workspaceId, user.getUserId()));

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
