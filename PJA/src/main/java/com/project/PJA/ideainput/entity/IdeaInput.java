package com.project.PJA.ideainput.entity;

import com.project.PJA.workspace.entity.Workspace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "idea_input")
public class IdeaInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idea_input_id")
    private Long ideaInputId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false, foreignKey = @ForeignKey(name = "FK_IDEA_INPUT_WORKSPACE"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workspace workspace;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_target")
    private String projectTarget;

    @Column(name = "project_description", columnDefinition = "TEXT")
    private String projectDescription;

    @Builder
    public IdeaInput(Workspace workspace, String projectName, String projectTarget, String projectDescription) {
        this.workspace = workspace;
        this.projectName = projectName;
        this.projectTarget = projectTarget;
        this.projectDescription = projectDescription;
    }

    public void updateProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void updateProjectTarget(String projectTarget) {
        this.projectTarget = projectTarget;
    }

    public void updateProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    /*public void update(String projectName, String projectTarget, String projectDescription) {
        this.projectName = projectName;
        this.projectTarget = projectTarget;
        this.projectDescription = projectDescription;
    }*/
}
