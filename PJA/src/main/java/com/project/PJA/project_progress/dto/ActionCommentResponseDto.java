package com.project.PJA.project_progress.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionCommentResponseDto {
    private Long commentId;
    private String content;
    private String authorName;
    private LocalDateTime updatedAt;
}
