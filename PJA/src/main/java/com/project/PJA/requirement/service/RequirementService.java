package com.project.PJA.requirement.service;

import com.project.PJA.exception.BadRequestException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.requirement.dto.RequirementContentRequest;
import com.project.PJA.requirement.dto.RequirementRequest;
import com.project.PJA.requirement.dto.RequirementResponse;
import com.project.PJA.requirement.entity.Requirement;
import com.project.PJA.requirement.repository.RequirementRepository;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequirementService {
    private final RequirementRepository requirementRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceService workspaceService;

    // 요구사항 명세서 조회
    @Transactional(readOnly = true)
    public List<RequirementResponse> getRequirement(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 비공개인데 멤버 아니면 403 반환
        workspaceService.validateWorkspaceAccess(userId, foundWorkspace);

        // 보여주기
        List<Requirement> requirements = requirementRepository.findByWorkspace_WorkspaceId(workspaceId);

        return requirements.stream()
                .map(req -> new RequirementResponse(
                        req.getRequirementId(),
                        req.getRequirementType(),
                        req.getContent()
                ))
                .collect(Collectors.toList());
    }

    // 요구사항 명세서 ai 생성 요청

    // 요구사항 명세서 저장
    @Transactional
    public List<RequirementResponse> saveRequirement(Long userId, Long workspaceId, List<RequirementRequest> requirementRequests) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrThrow(userId, foundWorkspace, "이 워크스페이스에 저장할 권한이 없습니다.");

        List<RequirementResponse> requirementResponses = new ArrayList<>();

        for (RequirementRequest request : requirementRequests) {
            if (request.getRequirementType() == null) {
                throw new BadRequestException("요구사항 타입이 비어있습니다.");
            }
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                throw new BadRequestException("요구사항 내용이 비어있습니다.");
            }

            Requirement savedRequirement = requirementRepository.save(Requirement.builder()
                    .workspace(foundWorkspace)
                    .requirementType(request.getRequirementType())
                    .content(request.getContent())
                    .build());

            requirementResponses.add(new RequirementResponse(
                    savedRequirement.getRequirementId(),
                    savedRequirement.getRequirementType(),
                    savedRequirement.getContent()
            ));
        }

        return requirementResponses;
    }

    // 요구사항 명세서 생성
    @Transactional
    public RequirementResponse createRequirement(Long userId, Long workspaceId, RequirementRequest requirementRequest) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

        Requirement createdRequirement = requirementRepository.save(
                Requirement.builder()
                        .workspace(foundWorkspace)
                        .requirementType(requirementRequest.getRequirementType())
                        .content(requirementRequest.getContent())
                        .build()
        );

        return new RequirementResponse(
                createdRequirement.getRequirementId(),
                createdRequirement.getRequirementType(),
                createdRequirement.getContent()
        );
    }

    // 요구사항 명세서 수정
    @Transactional
    public RequirementResponse updateRequirement(Long userId, Long workspaceId, Long requirementId, RequirementContentRequest requirementContentRequest) {
        Requirement foundRequirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new NotFoundException("요청하신 요구사항을 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 수정할 권한이 없습니다.");

        foundRequirement.update(requirementContentRequest.getContent());

        return new RequirementResponse(
                foundRequirement.getRequirementId(),
                foundRequirement.getRequirementType(),
                requirementContentRequest.getContent()
        );
    }

    // 요구사항 명세서 삭제
    @Transactional
    public RequirementResponse deleteRequirement(Long userId, Long workspaceId, Long requirementId) {
        Requirement foundRequirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new NotFoundException("요청하신 요구사항을 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 삭제할 권한이 없습니다.");

        requirementRepository.delete(foundRequirement);

        return new RequirementResponse(
                foundRequirement.getRequirementId(),
                foundRequirement.getRequirementType(),
                foundRequirement.getContent()
        );
    }
}
