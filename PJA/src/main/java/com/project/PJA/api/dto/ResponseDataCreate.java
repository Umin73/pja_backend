package com.project.PJA.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDataCreate {
    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private List<Data> data;
}
