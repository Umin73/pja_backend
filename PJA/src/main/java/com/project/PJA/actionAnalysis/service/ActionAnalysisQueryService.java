package com.project.PJA.actionAnalysis.service;

import com.project.PJA.actionAnalysis.dto.AssigneeDto;
import com.project.PJA.actionAnalysis.dto.AvgProcessingTimeGraphDto;
import com.project.PJA.actionAnalysis.dto.TaskImbalanceGraphDto;
import com.project.PJA.actionAnalysis.dto.TaskImbalanceResponseDto;
import com.project.PJA.actionAnalysis.repository.AvgProcessingTimeResultRepository;
import com.project.PJA.actionAnalysis.repository.TaskImbalanceResultRepository;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActionAnalysisQueryService {

    private final WorkspaceService workspaceService;
    private final WorkspaceRepository workspaceRepository;
    private final TaskImbalanceResultRepository taskImbalanceResultRepository;
    private final AvgProcessingTimeResultRepository avgProcessingTimeResultRepository;
    private final UserRepository userRepository;

    public TaskImbalanceResponseDto getTaskImbalanceGraph(Users user, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("워크스페이스 아이디로 워크스페이스를 찾을 수 없습니다."));

        workspaceService.validateWorkspaceAccess(user.getUserId(), foundWorkspace);

        List<TaskImbalanceGraphDto> graphData = taskImbalanceResultRepository.findLatestGroupedByWorkspaceMember(workspaceId);
        List<AssigneeDto> assignees = taskImbalanceResultRepository.findDistinctAssigneesByWorkspace(workspaceId);

        return new TaskImbalanceResponseDto(graphData, assignees);
    }

    public List<AssigneeDto> getDistinctAssignees(Long workspaceId) {
        return taskImbalanceResultRepository.findDistinctAssigneesByWorkspace(workspaceId);
    }


    public List<AvgProcessingTimeGraphDto> getAvgProcessingTimeGraph(Users user, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("워크스페이스 아이디로 워크스페이스를 찾을 수 없습니다."));

        workspaceService.validateWorkspaceAccess(user.getUserId(), foundWorkspace);

        Map<Long, String> userMap = userRepository.findAllById(
                avgProcessingTimeResultRepository.findByWorkspaceId(workspaceId).stream()
                        .map(r -> r.getUserId()).distinct().toList()
        ).stream().collect(Collectors.toMap(Users::getUserId, Users::getUsername));

        return avgProcessingTimeResultRepository.findByWorkspaceId(workspaceId)
                .stream().map(
                        result -> new AvgProcessingTimeGraphDto(
                                result.getUserId(),
                                userMap.getOrDefault(result.getUserId(), "알 수 없음"),
                                result.getImportance(),
                                result.getMeanHours()
                        )
                ).toList();
    }
}
