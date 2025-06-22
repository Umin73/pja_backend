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
@Table(name = "erd_column")
public class ErdColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erd_column_id")
    private Long erdColumnId; // 컬럼의 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "erd_table_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_erd_column_table"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ErdTable erdTable; // 해당 컬럼이 소한 테이블

    private String name; // 컬럼 이름

    @Column(name = "data_type")
    private String dataType; // 데이터 타입(VARCHAR, INT, ...)

    @Builder.Default
    @Column(name = "is_primary_key", nullable = false)
    private boolean isPrimaryKey = false; // 기본키 여부

    @Builder.Default
    @Column(name = "is_foreign_key", nullable = false)
    private boolean isForeignKey = false; // 외래키 여부

    @Builder.Default
    @Column(name = "is_nullable", nullable = false)
    private boolean isNullable = false; // null 허용 여부
}
