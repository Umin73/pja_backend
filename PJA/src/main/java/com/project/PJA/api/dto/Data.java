package com.project.PJA.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Data {
    private String field;
    private String type;
    private String example;
}
