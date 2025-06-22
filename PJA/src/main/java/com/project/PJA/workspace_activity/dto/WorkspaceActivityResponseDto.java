package com.project.PJA.workspace_activity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceActivityResponseDto {

    private String username; // 활동 멤버의 이름
    private String userProfile; // 활동 멤버의 프로필 이미지
    private String actionType; // 행동의 이름(생성, 수정, 삭제)
    private String targetType; // 행동한 대상(API, ERD, 액션, 멤버)
    private String relativeDateLabel; // 행동의 상대적 날짜(오늘, 3일 전, ...)
}
