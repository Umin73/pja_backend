package com.project.PJA.editing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdeaInputEditingUser {
    private Long userId;
    private String userName;
    private String userProfile;
    private String field;
    private Long fieldId;
    private String content;
}
