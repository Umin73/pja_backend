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
@Table(name = "main_function")
public class MainFunction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "main_function_id")
    private Long mainFunctionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_input_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MAIN_FUNCTION_IDEA_INPUT"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private IdeaInput ideaInput;

    @Column(name = "content")
    private String content;

    @Builder
    public MainFunction(IdeaInput ideaInput, String content) {
        this.ideaInput = ideaInput;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
    }
}
