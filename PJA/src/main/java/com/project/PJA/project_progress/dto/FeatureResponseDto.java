package com.project.PJA.project_progress.dto;

import com.project.PJA.project_progress.entity.Progress;
import com.project.PJA.workspace.entity.WorkspaceMember;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureResponseDto {

    private Long featureId; // 기능 아이디
    private String name; // 기능의 이름
    private Boolean state; // 진행 상태
    private Boolean hasTest; // 테스트 여부
    private Integer orderIndex; // 순서(리스트 상의 순서)
    private List<ActionResponseDto> actions;

}
