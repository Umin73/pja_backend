package com.project.PJA.user_act_log.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserActionLogParsing {
    private String event;
    private Long userId;
    private String username;
    private String timestamp;
    private Long workspaceId;
    private Details details;
}
