package com.project.PJA.workspace.enumeration;

public enum WorkspaceRole {
    OWNER, MEMBER, GUEST;

    public boolean isOwnerOrMember() {
        return this == OWNER || this == MEMBER;
    }
}
