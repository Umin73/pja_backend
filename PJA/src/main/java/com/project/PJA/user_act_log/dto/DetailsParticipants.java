package com.project.PJA.user_act_log.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailsParticipants {
    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("username")
    private String username;
}
