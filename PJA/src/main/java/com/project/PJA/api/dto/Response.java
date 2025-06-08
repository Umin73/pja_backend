package com.project.PJA.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private String statusCode;
    private String message;
    private List<Data> data;
}
