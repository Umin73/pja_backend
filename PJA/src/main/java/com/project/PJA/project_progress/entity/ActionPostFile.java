package com.project.PJA.project_progress.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "action_post_file")
public class ActionPostFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @Column(nullable = false)
    private String filePath;

    private String contentType; // image/png, application/pdf 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ActionPost actionPost;
}
