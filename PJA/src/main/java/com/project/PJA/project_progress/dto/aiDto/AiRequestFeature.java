package com.project.PJA.project_progress.dto.aiDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AiRequestFeature {
    private Long featureId;
    private String name;
    private List<AiRequestAction> aiFeature;
}
