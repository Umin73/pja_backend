package com.project.PJA.notification.repository;

import com.project.PJA.notification.entity.Notification;
import com.project.PJA.notification.entity.UserNotification;
import com.project.PJA.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    Optional<UserNotification> findByReceiverAndNotification_NotificationId(Users receiver, Long notificationId);

    List<UserNotification> findByReceiverAndWorkspaceId(Users receiver, Long workspaceId);
    List<UserNotification> findByReceiverAndWorkspaceIdOrderByCreatedAtDesc(Users receiver, long workspaceId);
    List<UserNotification> findByReceiverAndWorkspaceIdAndIsReadFalse(Users receiver, Long workspaceId);

    boolean existsByReceiverAndWorkspaceIdAndIsReadFalse(Users receiver, long workspaceId);
    boolean existsByNotification(Notification notification);

    long countByReceiverAndWorkspaceIdAndIsReadFalse(Users receiver, Long workspaceId);
}
