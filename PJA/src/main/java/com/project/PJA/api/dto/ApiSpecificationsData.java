package com.project.PJA.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiSpecificationsData {
    @JsonProperty("title")
    private String title;

    @JsonProperty("tag")
    private String tag;

    @JsonProperty("path")
    private String path;

    @JsonProperty("http_method")
    private String httpMethod;

    @JsonProperty("request")
    private List<Data> request;

    @JsonProperty("response")
    private List<ResponseDataCreate> response;
}
