package com.project.PJA.erd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "erd_column")
public class ErdColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erd_column_id")
    private Long erdColumnId; // 컬럼의 id

    @ManyToOne
    @JoinColumn(name = "erd_table_id", nullable = false)
    private ErdTable erdTable; // 해당 컬럼이 소한 테이블

    private String name; // 컬럼 이름

    @Column(name = "data_type")
    private String dataType; // 데이터 타입(VARCHAR, INT, ...)

    @Column(name = "is_primary_key", nullable = false)
    private boolean isPrimaryKey; // 기본키 여부

    @Column(name = "is_foreign_key", nullable = false)
    private boolean isForeignKey; // 외래키 여부

    @Column(name = "is_nullable", nullable = false)
    private boolean isNullable; // null 허용 여부
}
