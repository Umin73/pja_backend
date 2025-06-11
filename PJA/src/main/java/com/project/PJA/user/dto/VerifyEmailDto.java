package com.project.PJA.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyEmailDto {
    private String email;
    private String token;
}
