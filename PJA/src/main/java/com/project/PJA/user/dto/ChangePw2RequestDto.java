package com.project.PJA.user.dto;

import lombok.Getter;

@Getter
public class ChangePw2RequestDto {
    private String uid;
    private String newPw;
    private String confirmPw;
}
