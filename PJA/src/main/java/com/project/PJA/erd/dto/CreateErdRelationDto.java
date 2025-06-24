package com.project.PJA.erd.dto;

import com.project.PJA.erd.entity.ErdRelation;
import lombok.Getter;

@Getter
public class CreateErdRelationDto {
    private String fromTableId;      // 외래키 컬럼이 있는 테이블
    private String toTableId;        // 참조 대상 테이블
    private String foreignKeyId;     // 외래키 컬럼 아이디
    private String toTableKeyId;    // 참조 대상 컬럼의 아이디
    private String foreignKeyName; // 외래키 컬럼 이름
    private String constraintName; // 제약조건 이름 (선택)
    private ErdRelation type;      // 관계 타입 (ONE_TO_MANY, MANY_TO_MANY 등)
}