package com.project.PJA.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {

    private String uid;
    private String password;
    private String name;
    private String email;
}
