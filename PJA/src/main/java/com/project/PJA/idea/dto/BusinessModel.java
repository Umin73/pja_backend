package com.project.PJA.idea.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessModel {
    private String type;

    @JsonProperty("revenue_streams")
    private List<String> revenueStreams;

    @JsonProperty("target_market")
    private String targetMarket;
}
