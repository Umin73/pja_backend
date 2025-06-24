package com.project.PJA.editing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdeaInputEditingRequest {
    private String field;
    private Long fieldId;
    private String content;
}
