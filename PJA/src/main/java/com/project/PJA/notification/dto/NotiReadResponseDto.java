package com.project.PJA.notification.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotiReadResponseDto {
    private Long notificationId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Long actionPostId;
    private Long actionId;
}
