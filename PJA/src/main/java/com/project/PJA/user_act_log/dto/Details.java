package com.project.PJA.user_act_log.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Details {
    private Long actionId;
    private String name;
    private String state;
    private Integer importance;
    private String startDate;
    private String endDate;
    private List<DetailsParticipants> participants;
}
