package com.project.PJA.erd.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "erd_Table")
public class ErdTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erd_table_id")
    private Long erdTableId; // 테이블 고유 ID

    @Column(name = "name")
    private String name; // 테이블 이름

    @ManyToOne
    @JoinColumn(name = "erd_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Erd erd; // 해당 테이블이 속한 ERD

    @Builder.Default
    @OneToMany(mappedBy = "erdTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ErdColumn> columns = new ArrayList<>(); // 해당 테이블의 컬럼 리스트

    @Builder.Default
    @OneToMany(mappedBy = "fromTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ErdRelationships> fromRelationships = new ArrayList<>(); // 이 테이블이 from(출발지?)인 관계들

    @Builder.Default
    @OneToMany(mappedBy = "toTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ErdRelationships> toRelationships = new ArrayList<>(); // 이 테이블이 to(도착지?)인 관계들

    @Column(name = "position_x")
    private Double positionX;

    @Column(name = "position_y")
    private Double positionY;
}
