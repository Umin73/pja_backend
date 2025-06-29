package com.project.PJA.sse.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SendEmitterService {

    @Async
    public void sendEmitter(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 성공")
                    .reconnectTime(10_000L));
        } catch (Exception e) {
            throw new RuntimeException("SSE 연결 오류가 발생했습니다.", e);
        }
    }
}
