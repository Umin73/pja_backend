package com.project.PJA.user.dto;

import lombok.Getter;

@Getter
public class ChangePwRequestDto {
    private String currentPw;
    private String newPw;
    private String confirmPw;
}
