package com.project.PJA.notification.repository;

import com.project.PJA.notification.entity.Notification;
import com.project.PJA.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
