package com.project.PJA.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotiReadResponseDto {
    private Long notificationId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Long actionPostId;
}
