package com.project.PJA.workspace_activity.service;

import com.project.PJA.exception.NotFoundException;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    public WorkspaceActivityService(WorkspaceActivityRepository workspaceActivityRepository, WorkspaceRepository workspaceRepository, WorkspaceService workspaceService, UserRepository userRepository) {
        this.workspaceActivityRepository = workspaceActivityRepository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceService = workspaceService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addWorkspaceActivity(Users user, Long workspaceId, ActivityTargetType targetType, ActivityActionType actionType) {
        WorkspaceActivity workspaceActivity = new WorkspaceActivity();
        workspaceActivity.setUserId(user.getUserId());
//        workspaceActivity.setUsername(user.getUsername());
//        workspaceActivity.setUserProfile(user.getProfileImage());
        workspaceActivity.setWorkspaceId(workspaceId);
        workspaceActivity.setTargetType(targetType);
        workspaceActivity.setActionType(actionType);
        workspaceActivity.setCreatedAt(LocalDateTime.now());

        workspaceActivityRepository.save(workspaceActivity);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceActivityResponseDto> getWorkspaceActivities(Users user, Long workspaceId) {

        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("워크스페이스 아이디로 워크스페이스를 찾지 못했습니다."));

        workspaceService.validateWorkspaceAccess(user.getUserId(), foundWorkspace);

        List<WorkspaceActivity> workspaceActivityList = workspaceActivityRepository.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId);
        List<WorkspaceActivityResponseDto> dtoList = new ArrayList<>();

        for (WorkspaceActivity activity : workspaceActivityList) {
            WorkspaceActivityResponseDto dto = new WorkspaceActivityResponseDto();

            Users foundUser = userRepository.findById(activity.getUserId())
                            .orElseThrow(()
                                    -> new NotFoundException("[WorkspaceActivityService] getWorkspaceActivities에서 유저 정보를 찾을 수 없습니다."));

            dto.setUsername(foundUser.getUsername());
            dto.setUserProfile(foundUser.getProfileImage());
            dto.setActionType(activity.getActionType().getKorean());
            dto.setTargetType(activity.getTargetType().getKorean());
            dto.setRelativeDateLabel(getRelativeDate(activity.getCreatedAt()));

            dtoList.add(dto);
        }

        return dtoList;
    }

    private String getRelativeDate(LocalDateTime dateTime) {
        if(dateTime == null) {
            return "";
        }

        LocalDate today = LocalDate.now();
        LocalDate targetDate = dateTime.toLocalDate();

        long daysBetween = ChronoUnit.DAYS.between(targetDate, today);

        if (daysBetween == 0) {
            return "오늘";
        } if (daysBetween == 1) {
            return "어제";
        } else {
            return daysBetween + "일 전";
        }
    }

}
