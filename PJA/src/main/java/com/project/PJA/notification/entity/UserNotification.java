package com.project.PJA.notification.entity;

import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity(name = "user_notification")
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_notification_id")
    private Long userNotificationId; // 알림 받는 사람 아이디

    @Column(name = "is_read")
    private boolean isRead = false; // 읽었는지 여부

    private Long workspaceId; // 알림이 속하는 워크스페이스의 아이디

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users receiver; // 알림 받는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id")
    private Notification notification; // 알림
}
