package com.project.PJA.notification.service;

import com.project.PJA.notification.dto.NotiReadResponseDto;
import com.project.PJA.notification.entity.Notification;
import com.project.PJA.sse.repository.SseEmitterRepository;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotiAsyncService {

    private final SseEmitterRepository sseEmitterRepository;

    @Async
    public void sendNotificationAsync(Notification notification, List<Users> receivers, Long workspaceId) {
        for(Users receiver : receivers) {
            try {
                sseEmitterRepository.get(workspaceId, receiver.getUid())
                        .ifPresent(emitter -> {
                            try {
                                NotiReadResponseDto notiDto = NotiReadResponseDto.builder()
                                        .notificationId(notification.getNotificationId())
                                        .message(notification.getMessage())
                                        .isRead(false)
                                        .actionPostId(notification.getActionPost() != null ? notification.getActionPost().getActionPostId() : null)
                                        .createdAt(notification.getCreatedAt())
                                        .build();

                                emitter.send(SseEmitter.event()
                                        .name("notification")
                                        .data(notiDto));
                            } catch (IOException e) {
                                log.warn("SSE 전송 실패 - emitter 제거: {}", e.getMessage());
                                emitter.completeWithError(e);
                                sseEmitterRepository.delete(workspaceId, receiver.getUid());
                            }
                        });

            } catch (Exception e) {
                log.error("SSE emitter 조회 중 오류가 발생했습니다: {}", e.getMessage());
            }
        }
    }
}
