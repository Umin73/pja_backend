package com.project.PJA.git_interlocking.entity;

import com.project.PJA.workspace.entity.Workspace;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "git")
public class GitInterlocking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "git_id")
    private Long gitId;

    @Column(name = "git_url")
    private String gitUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "FK_GIT_WORKSPACE"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workspace workspace;
}
