package com.project.PJA.requirement.service;

import com.project.PJA.exception.NotFoundException;
import com.project.PJA.requirement.dto.RequirementDto;
import com.project.PJA.requirement.dto.RequirementResponse;
import com.project.PJA.requirement.entity.Requirement;
import com.project.PJA.requirement.repository.RequirementRepository;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequirementService {
    private final RequirementRepository requirementRepository;
    private final WorkspaceRepository workspaceRepository;

    // 요구사항 명세서 조회
    @Transactional(readOnly = true)
    public RequirementResponse getRequirement(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 비공개인데 멤버 아니면 403 반환

        // 보여주기
        List<Requirement> requirements = requirementRepository.findByWorkspace_WorkspaceId(workspaceId);

        List<RequirementDto> requirementDtos = requirements.stream()
                .map(req -> new RequirementDto(
                        req.getRequirementId(),
                        req.getRequirementType(),
                        req.getContent()
                ))
                .collect(Collectors.toList());

        return new RequirementResponse(workspaceId, requirementDtos);
    }
}
