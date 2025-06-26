package com.project.PJA.workspace_activity.entity;

import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace_activity.enumeration.ActivityActionType;
import com.project.PJA.workspace_activity.enumeration.ActivityTargetType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "workspace_activity")
public class WorkspaceActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_activity_id")
    private Long workspaceActivityId;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @Column(name = "user_id")
    private Long userId;
//    private String username;
//    @Column(name = "user_profile")
//    private String userProfile;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "target_type")
    private ActivityTargetType targetType;

    @Column(name = "action_type")
    private ActivityActionType actionType;
}
