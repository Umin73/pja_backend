package com.project.PJA.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class RefreshToken {
    @Id
    private String userId;

    private String token;

    private LocalDateTime expiresAt;
}
