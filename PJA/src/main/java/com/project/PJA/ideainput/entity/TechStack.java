package com.project.PJA.ideainput.entity;

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
@Table(name = "tech_stack")
public class TechStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tech_stack_id")
    private Long techStackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_input_id", nullable = false, foreignKey = @ForeignKey(name = "FK_TECH_STACK_IDEA_INPUT"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private IdeaInput ideaInput;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Builder
    public TechStack(IdeaInput ideaInput, String content) {
        this.ideaInput = ideaInput;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
    }
}
