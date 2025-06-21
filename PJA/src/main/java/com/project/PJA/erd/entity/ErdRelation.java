package com.project.PJA.erd.entity;

public enum ErdRelation {
    ONE_TO_ONE, // 1:1 관계
    ONE_TO_MANY, // 1:N 관계
    MANY_TO_ONE, // N:N 관계
    MANY_TO_MANY, // M:N 관계
    SELF_REFERENCE, // 자기 참조 관계
    INHERITANCE; // 상속 관계

    public static ErdRelation fromString(String value) {
        return switch (value.toLowerCase()) {
            case "one-to-one" -> ONE_TO_ONE;
            case "one-to-many" -> ONE_TO_MANY;
            case "many-to-one" -> MANY_TO_ONE;
            case "many-to-many" -> MANY_TO_MANY;
            case "self-reference" -> SELF_REFERENCE;
            case "inheritance" -> INHERITANCE;
            default -> throw new IllegalArgumentException("Unknown relation type: " + value);
        };
    }
}
