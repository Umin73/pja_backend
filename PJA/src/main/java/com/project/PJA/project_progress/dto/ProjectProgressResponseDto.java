package com.project.PJA.project_progress.dto;

import com.project.PJA.workspace.entity.WorkspaceMember;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectProgressResponseDto {

    //프로젝트 주요 기능
    private List<String> coreFeatures = new ArrayList<>();

    //참여자 Set
    private Set<WorkspaceMemberDto> participants = new HashSet<>();

    // 카테고리 리스트
    private List<FeatureCategoryResponseDto> featureCategories = new ArrayList<>();
}
