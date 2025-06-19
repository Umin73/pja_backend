package com.project.PJA.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.project.PJA.common.config.ForceStringDeserializer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Data {
    private String field;
    private String type;
    @JsonDeserialize(using = ForceStringDeserializer.class)
    private String example;
}
