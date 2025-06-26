package com.project.PJA.actionAnalysis.service;

import com.project.PJA.actionAnalysis.dto.AssigneeDto;
import com.project.PJA.actionAnalysis.dto.AvgProcessingTimeGraphDto;
import com.project.PJA.actionAnalysis.dto.TaskImbalanceGraphDto;
import com.project.PJA.actionAnalysis.dto.TaskImbalanceResponseDto;
import com.project.PJA.actionAnalysis.entity.AvgProcessingTimeResult;
import com.project.PJA.actionAnalysis.entity.TaskImbalanceResult;
import com.project.PJA.actionAnalysis.repository.AvgProcessingTimeResultRepository;
import com.project.PJA.actionAnalysis.repository.TaskImbalanceResultRepository;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.enumeration.WorkspaceRole;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActionAnalysisQueryService {

    private final WorkspaceService workspaceService;
    private final WorkspaceRepository workspaceRepository;
    private final TaskImbalanceResultRepository taskImbalanceResultRepository;
    private final AvgProcessingTimeResultRepository avgProcessingTimeResultRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public TaskImbalanceResponseDto getTaskImbalanceGraph(Users user, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("워크스페이스 아이디로 워크스페이스를 찾을 수 없습니다."));

        workspaceService.validateWorkspaceAccess(user.getUserId(), foundWorkspace);

        List<TaskImbalanceResult> results = taskImbalanceResultRepository.findByWorkspaceId(workspaceId);

        Set<WorkspaceMember> members = workspaceMemberRepository.findAllByWorkspace_WorkspaceIdAndWorkspaceRoleNot(workspaceId, WorkspaceRole.GUEST);

        List<TaskImbalanceGraphDto> graphData = results.stream()
                .map(r -> new TaskImbalanceGraphDto(
                        r.getUserId(), // or workspaceMemberId if available
                        userRepository.findById(r.getUserId()).map(Users::getUsername).orElse("알 수 없는 사용자"),
                        r.getState(),
                        r.getImportance(),
                        r.getTaskCount().longValue()
                ))
                .toList();

        List<AssigneeDto> assignees = new ArrayList<>();
        for (WorkspaceMember member : members) {
            AssigneeDto assigneeDto = new AssigneeDto();

            assigneeDto.setUsername(member.getUser().getUsername());
            assigneeDto.setUserId(member.getUser().getUserId());

            assignees.add(assigneeDto);
        }

        return new TaskImbalanceResponseDto(graphData, assignees);
    }

    public List<AvgProcessingTimeGraphDto> getAvgProcessingTimeGraph(Users user, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("워크스페이스 아이디로 워크스페이스를 찾을 수 없습니다."));

        workspaceService.validateWorkspaceAccess(user.getUserId(), foundWorkspace);

        List<AvgProcessingTimeResult> results = avgProcessingTimeResultRepository.findByWorkspaceId(workspaceId);

        Map<Long, String> userMap = userRepository.findAllById(
                results.stream().map(AvgProcessingTimeResult::getUserId).distinct().toList()
        ).stream().collect(Collectors.toMap(Users::getUserId, Users::getUsername));

        return results.stream()
                .map(result -> new AvgProcessingTimeGraphDto(
                        result.getUserId(),
                        userMap.getOrDefault(result.getUserId(), "알 수 없는 사용자"),
                        result.getImportance(),
                        result.getMeanHours()
                ))
                .toList();

    }
}
