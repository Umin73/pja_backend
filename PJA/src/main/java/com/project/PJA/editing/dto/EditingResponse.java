package com.project.PJA.editing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditingResponse {
    private Long userId;
    private String userName;
    private String page;
    private String field;
    private String status;
}
