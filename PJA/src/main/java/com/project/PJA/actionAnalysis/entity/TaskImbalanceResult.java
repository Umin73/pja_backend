package com.project.PJA.actionAnalysis.entity;

import com.project.PJA.project_progress.entity.Progress;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_imbalance_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskImbalanceResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_imbalance_result_id", nullable = false)
    private Long taskImbalanceResultId;

    @Column(name = "workspace_id")
    private Long workspaceId;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Progress state;

    private Integer importance;

    @Column(name = "task_count")
    private Integer taskCount;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;
}
