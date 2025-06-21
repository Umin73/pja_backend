package com.project.PJA.workspace_activity.service;

import com.project.PJA.exception.NotFoundException;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import com.project.PJA.workspace_activity.dto.WorkspaceActivityResponseDto;
import com.project.PJA.workspace_activity.entity.WorkspaceActivity;
import com.project.PJA.workspace_activity.enumeration.ActivityActionType;
import com.project.PJA.workspace_activity.enumeration.ActivityTargetType;
import com.project.PJA.workspace_activity.repository.WorkspaceActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WorkspaceActivityService {

    private final WorkspaceActivityRepository workspaceActivityRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceService workspaceService;

    public WorkspaceActivityService(WorkspaceActivityRepository workspaceActivityRepository, WorkspaceRepository workspaceRepository, WorkspaceService workspaceService) {
        this.workspaceActivityRepository = workspaceActivityRepository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceService = workspaceService;
    }

    @Transactional
    public void addWorkspaceActivity(Users user, Long workspaceId, ActivityTargetType targetType, ActivityActionType actionType) {

        WorkspaceActivity workspaceActivity = new WorkspaceActivity();
        workspaceActivity.setUser(user);
        workspaceActivity.setWorkspaceId(workspaceId);
        workspaceActivity.setTargetType(targetType);
        workspaceActivity.setActionType(actionType);

        workspaceActivityRepository.save(workspaceActivity);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceActivityResponseDto> getWorkspaceActivities(Users user, Long workspaceId) {

        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("워크스페이스 아이디로 워크스페이스를 찾지 못했습니다."));

        workspaceService.validateWorkspaceAccess(user.getUserId(), foundWorkspace);

        List<WorkspaceActivity> workspaceActivityList = workspaceActivityRepository.findByWorkspaceId(workspaceId);
        List<WorkspaceActivityResponseDto> dtoList = new ArrayList<>();

        for (WorkspaceActivity activity : workspaceActivityList) {
            WorkspaceActivityResponseDto dto = new WorkspaceActivityResponseDto();

            dto.setUsername(activity.getUser().getUsername());
            dto.setUserProfile(activity.getUser().getProfileImage());
            dto.setActionType(activity.getActionType().toString()); // 한글로 수정 필요?
            dto.setTargetType(activity.getTargetType().toString()); // 한글로 수정 필요?
            dto.setRelativeDateLabel(getRelativeDate(activity.getCreatedAt()));

            dtoList.add(dto);
        }

        return dtoList;
    }

    private String getRelativeDate(LocalDateTime dateTime) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = dateTime.toLocalDate();

        long daysBetween = ChronoUnit.DAYS.between(targetDate, today);

        if (daysBetween == 0) {
            return "오늘";
        } else {
            return daysBetween + "일 전";
        }
    }

}
