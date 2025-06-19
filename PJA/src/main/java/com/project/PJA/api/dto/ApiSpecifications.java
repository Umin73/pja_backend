package com.project.PJA.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiSpecifications {
    private List<ApiSpecificationsData> apiSpecifications;
}
