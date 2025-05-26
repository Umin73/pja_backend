package com.project.PJA.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RefreshToken implements Serializable {
    private int userId;
    private String token;
    private LocalDateTime expiresAt;
}
