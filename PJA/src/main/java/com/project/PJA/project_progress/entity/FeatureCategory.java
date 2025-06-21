package com.project.PJA.project_progress.entity;

import com.project.PJA.workspace.entity.Workspace;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "feature_category")
public class FeatureCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feature_category_id")
    public Long featureCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workspace workspace; // 워크스페이스

    private String name; // 이름

    @Builder.Default
    private Boolean state = false; //상태

    @Column(name = "has_test")
    private Boolean hasTest; // 테스트 여부

    @Column(name = "order_index")
    private Integer orderIndex; // 순서(리스트 상의 순서)

    @Builder.Default
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feature> features = new ArrayList<>();
}
