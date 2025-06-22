package com.project.PJA.erd.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "erd_relationships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_erd_table_id", "foreign_key"}))
public class ErdRelationships {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erd_relationships_id")
    private Long erdRelationshipsId; // erd 관계의 기본키!


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ErdRelation type; // 관계 유형

    @Column(name = "foreign_key", nullable = false)
    private String foreignKeyName;

    @ManyToOne
    @JoinColumn(name = "foreign_column_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ErdColumn foreignColumn;

    @ManyToOne
    @JoinColumn(name = "referenced_column_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ErdColumn referencedColumn;

    @Column(name = "constraint_name")
    private String constraintName; // DB 제약 조건

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_erd_table_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_erd_relationships_from"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ErdTable fromTable; // 관계의 출발 테이블

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_erd_table_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_erd_relationships_to"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ErdTable toTable; // 관계의 대상 테이블
}
