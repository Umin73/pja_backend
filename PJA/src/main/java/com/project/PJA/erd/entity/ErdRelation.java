package com.project.PJA.erd.entity;

public enum ErdRelation {
    ONE_TO_ONE, // 1:1 관계
    ONE_TO_MANY, // 1:N 관계
    MANY_TO_ONE, // N:N 관계
    MANY_TO_MANY, // M:N 관계
    SELF_REFERENCE, // 자기 참조 관계
    INHERITANCE, // 상속 관계
}
