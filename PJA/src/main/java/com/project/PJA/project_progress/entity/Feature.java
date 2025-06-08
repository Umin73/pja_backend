package com.project.PJA.project_progress.entity;

import com.project.PJA.workspace.entity.WorkspaceMember;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "feature")
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feature_id")
    private Long featureId;

    private String name; // 이름

    @Column(name = "start_date")
    private LocalDateTime startDate; // 시작일

    @Column(name = "end_date")
    private LocalDateTime endDate; // 마감일

    @Enumerated(EnumType.STRING)
    private Progress state; //상태

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer importance; // 중요도

    @Column(name = "order_index")
    private Integer orderIndex; // 순서(리스트 상의 순서)

    @ManyToMany
    @JoinTable(
            name = "feature_participant",
            joinColumns = @JoinColumn(name = "feature_id"),
            inverseJoinColumns = @JoinColumn(name = "workspace_member_id")
    )
    private Set<WorkspaceMember> participants = new HashSet<>(); // 참여자(워크스페이스 멤버로만 한정)


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private FeatureCategory category;

    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Action> actions = new ArrayList<>();
}
