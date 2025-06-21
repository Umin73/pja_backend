package com.project.PJA.user_act_log.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.PJA.user_act_log.enumeration.UserActionType;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserActionLog {
    @JsonProperty("event")
    private UserActionType event;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("username")
    private String username;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("timestamp")
    private ZonedDateTime timestamp;
    @JsonProperty("workspaceId")
    private Long workspaceId;
    @JsonProperty("details")
    private Map<String, Object> details;
}
