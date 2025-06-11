package com.project.PJA.project_progress.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "action_post")
public class ActionPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_post_id")
    private Long actionPostId;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content = "";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", unique = true)
    private Action action;

    @Builder.Default
    @OneToMany(mappedBy = "actionPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActionComment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "actionPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActionPostFile> actionPostFiles = new ArrayList<>();
}
