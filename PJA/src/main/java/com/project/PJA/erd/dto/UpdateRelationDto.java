package com.project.PJA.erd.dto;

import com.project.PJA.erd.entity.ErdRelation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class UpdateRelationDto {
    private ErdRelation type;      // 관계 타입 (ONE_TO_MANY, MANY_TO_MANY 등)
}
