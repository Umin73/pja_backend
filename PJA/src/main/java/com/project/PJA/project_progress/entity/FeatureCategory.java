package com.project.PJA.project_progress.entity;

import com.project.PJA.workspace.entity.Workspace;
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
    private Workspace workspace; // 워크스페이스

    @ManyToMany
    @JoinTable(
            name = "feature_category_participant",
            joinColumns = @JoinColumn(name = "feature_category_id"),
            inverseJoinColumns = @JoinColumn(name = "workspace_member_id")
    )
    @Builder.Default
    private Set<WorkspaceMember> participants = new HashSet<>(); // 참여자(워크스페이스 멤버로만 한정)

    private String name; // 이름

    @Column(name = "start_date")
    private LocalDateTime startDate; // 시작일

    @Column(name = "end_date")
    private LocalDateTime endDate; // 마감일

    @Enumerated(EnumType.STRING)
    private Progress state; //상태

    @Column(name = "has_test")
    private Boolean hasTest; // 테스트 여부

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer importance; // 중요도

    @Column(name = "order_index")
    private Integer orderIndex; // 순서(리스트 상의 순서)

    @Builder.Default
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feature> features = new ArrayList<>();


}
