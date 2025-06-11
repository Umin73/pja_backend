package com.project.PJA.project_progress.dto;

import com.project.PJA.workspace.enumeration.WorkspaceRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceMemberDto {
    private Long memberId;
    private String username;
    private String profileImage;
    private WorkspaceRole role;
}
