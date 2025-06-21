package com.project.PJA.member.dto;

import com.project.PJA.workspace.enumeration.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long memberId;
    private String name;
    private String email;
    private String profile;
    private WorkspaceRole workspaceRole;
    private LocalDateTime joinedAt;
}
