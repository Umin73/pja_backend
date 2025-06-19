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
    //@JsonDeserialize(using = ForceStringDeserializer.class)
    private Object example;
}
