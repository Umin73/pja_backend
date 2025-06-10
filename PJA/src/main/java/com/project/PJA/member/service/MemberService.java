package com.project.PJA.member.service;

import com.project.PJA.exception.NotFoundException;
import com.project.PJA.member.dto.MemberRequest;
import com.project.PJA.member.dto.MemberResponse;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final WorkspaceService workspaceService;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.validateWorkspaceAccess(userId, foundWorkspace);

        List<WorkspaceMember> foundWorkspaceMembers = workspaceMemberRepository.findAllByWorkspace_WorkspaceId(workspaceId);

        return foundWorkspaceMembers.stream()
                .map(member -> new MemberResponse(
                        member.getUser().getUserId(),
                        member.getUser().getName(),
                        member.getUser().getEmail(),
                        member.getWorkspaceRole(),
                        member.getJoinedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public MemberResponse updateMember(Long userId, Long workspaceId, MemberRequest memberRequest) {
        WorkspaceMember foundWorkspaceMember = workspaceMemberRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, memberRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스와 사용자를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrThrow(userId, workspaceId, "멤버 역할을 수정할 권한이 없습니다.");

        foundWorkspaceMember.update(memberRequest.getWorkspaceRole());

        return new MemberResponse(
                foundWorkspaceMember.getUser().getUserId(),
                foundWorkspaceMember.getUser().getName(),
                foundWorkspaceMember.getUser().getEmail(),
                memberRequest.getWorkspaceRole(),
                foundWorkspaceMember.getJoinedAt()
        );
    }

    // 팀원 삭제
    @Transactional
    public MemberResponse deleteMember(Long userId, Long workspaceId, Long memberId) {
        WorkspaceMember foundWorkspaceMember = workspaceMemberRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, memberId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스와 사용자를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrThrow(userId, workspaceId, "멤버를 삭제할 권한이 없습니다.");

        workspaceMemberRepository.delete(foundWorkspaceMember);

        return new MemberResponse(
                foundWorkspaceMember.getUser().getUserId(),
                foundWorkspaceMember.getUser().getName(),
                foundWorkspaceMember.getUser().getEmail(),
                foundWorkspaceMember.getWorkspaceRole(),
                foundWorkspaceMember.getJoinedAt()
        );
    }
}
