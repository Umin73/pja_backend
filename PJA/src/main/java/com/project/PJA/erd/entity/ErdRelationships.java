package com.project.PJA.erd.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "erd_relationships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_erd_table_id", "foreign_key"}))
public class ErdRelationships {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erd_relationships_id")
    private Long erdRelationshipsId; // erd 관계의 기본키


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ErdRelation type; // 관계 유형

    @Column(name = "foreign_key", nullable = false)
    private String foreignKeyName;

    @ManyToOne
    @JoinColumn(name = "foreign_column_id", nullable = false)
    private ErdColumn foreignColumn;

    @Column(name = "constraint_name")
    private String constraintName; // DB 제약 조건

    @ManyToOne
    @JoinColumn(name = "from_erd_table_id", nullable = false)
    private ErdTable fromTable; // 관계의 출발 테이블

    @ManyToOne
    @JoinColumn(name = "to_erd_table_id", nullable = false)
    private ErdTable toTable; // 관계의 대상 테이블
}
