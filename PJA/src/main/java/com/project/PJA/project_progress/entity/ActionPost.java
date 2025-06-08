package com.project.PJA.project_progress.entity;

import com.project.PJA.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "action_post")
public class ActionPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_post_id")
    private Long actionPostId;

    @Lob
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", unique = true)
    private Action action;

    @OneToMany(mappedBy = "actionPost", cascade = CascadeType.ALL)
    private List<ActionComment> comments = new ArrayList<>();
}
