package com.project.PJA.erd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "erd")
public class Erd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "erd_id")
    private Long erdId; // ERD의 고유 ID

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성 일시

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId; // ERD가 속한 워크스페이스 ID

    @OneToMany(mappedBy = "erd", cascade = CascadeType.ALL)
    private List<ErdTable> tables = new ArrayList<>(); // ERD에 포함된 테이블들(1:N)
}
