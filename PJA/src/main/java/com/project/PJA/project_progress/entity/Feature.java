package com.project.PJA.project_progress.entity;

import com.project.PJA.workspace.entity.WorkspaceMember;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "feature")
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feature_id")
    private Long featureId;

    private String name; // 이름

    @Builder.Default
    private Boolean state = false; //상태

    @Column(name = "has_test")
    private Boolean hasTest; // 테스트 여부

    @Column(name = "order_index")
    private Integer orderIndex; // 순서(리스트 상의 순서)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private FeatureCategory category;

    @Builder.Default
    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Action> actions = new ArrayList<>();
}
