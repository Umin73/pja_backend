package com.project.PJA.workspace_activity.enumeration;

public enum ActivityTargetType {
    IDEA("프로젝트 아이디어"), // OK
    REQUIREMENT("요구사항 명세서"), // OK
    PROJECT_INFO("프로젝트 정보"), // OK
    ERD("ERD"),
    API("API 명세서"), // OK
    ACTION("ACTION"), // OK
    MEMBER("멤버"), // OK
    ROLE("역할"), // OK
    WORKSPACE_SETTING("워크스페이스 정보"), // OK
    GIT("GIT"); // OK

    private final String korean;

    ActivityTargetType(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }
}
