package com.project.PJA.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {

    private String uid;
    private String password;
    private String name;
    private String email;
}
