package com.project.PJA.erd.dto.aiGenerateDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErdSpecifications {
    private List<ErdSpecificationsData> erdSpecifications;
}
