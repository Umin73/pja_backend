package com.project.PJA.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequest {
    private String title;
    private String tag;
    private String path;
    private String httpMethod;
    private List<Data> request;
    private List<Response> response;
}
