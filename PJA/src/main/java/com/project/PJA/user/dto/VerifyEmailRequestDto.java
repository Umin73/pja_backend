package com.project.PJA.user.dto;

import lombok.Getter;

@Getter
public class VerifyEmailRequestDto {
    private String email;
    private String token;
}
