package com.project.PJA.workspace.dto;

import com.project.PJA.workspace.enumeration.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceAuthCache {
    private Boolean isPublic;
    private List<MemberRoles> memberRoles;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberRoles{
        private Long userId;
        private WorkspaceRole workspaceRole;
    }
}
