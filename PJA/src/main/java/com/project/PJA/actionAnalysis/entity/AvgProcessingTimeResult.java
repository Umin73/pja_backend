package com.project.PJA.actionAnalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "avg_processing_time_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvgProcessingTimeResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "avg_processing_time_result_id", nullable = false)
    private Long avgProcessingTimeResultId;

    @Column(name = "workspace_id")
    private Long workspaceId;

    @Column(name = "user_id")
    private Long userId;

    private Integer importance;

    @Column(name = "mean_hours")
    private Long meanHours;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;
}
