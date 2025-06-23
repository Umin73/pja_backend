package com.project.PJA.workspace_activity.enumeration;

public enum ActivityActionType {
    CREATE("생성"),
    UPDATE("수정"),
    DELETE("삭제"),
    JOIN("참여"),
    LEAVE("탙퇴"),
    CHANGE("변경"); // 역할 변경

    private final String korean;

    ActivityActionType(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }
}
