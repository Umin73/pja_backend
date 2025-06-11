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
public class FeatureCategoryResponseDto {

    private Long featureCategoryId; // 기능 카테고리 아이디
    private Set<WorkspaceMemberDto> participants = new HashSet<>(); // 해당 기능 카테고리의 참여자
    private String name; // 기능 카테고리의 이름
    private LocalDateTime startDate; // 시작일
    private LocalDateTime endDate; // 마감일
    private Progress state; // 진행 상태
    private Boolean hasTest; // 테스트 여부
    private Integer importance; // 중요도
    private Integer orderIndex; // 순서(리스트 상의 순서)
    private List<FeatureResponseDto> features;

}
