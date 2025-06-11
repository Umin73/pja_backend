package com.project.PJA.erd.dto.reactFlowtDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdgeDto {
    private String id; // id의 타입을 String으로 변환해서 프론트로 넘겨야 함
    private String source; // 관계의 출발 테이블(from)
    private String target; //  관계의 대상 테이블(to)
    private String sourceHandle;
    private String targetHandle;
    private String label; // "1:N", "N:M", ...
}
