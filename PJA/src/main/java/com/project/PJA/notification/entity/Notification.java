package com.project.PJA.notification.entity;

import com.project.PJA.project_progress.entity.ActionPost;
import com.project.PJA.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId; // 알림 아이디

    private String message; // 알림 메세지 내용

    @Column(name = "created_At")
    private LocalDateTime createdAt; // 알림 생성일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_post")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ActionPost actionPost; // 알림과 관련된 액션 포스트

    @Builder.Default
    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<UserNotification> receivers = new ArrayList<>(); // 알림 받는 사람들

    public void addUserNotification(UserNotification userNotification) {
        this.receivers.add(userNotification);
        userNotification.setNotification(this);
    }
}
