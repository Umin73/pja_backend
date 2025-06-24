package com.project.PJA.member.service;

import com.project.PJA.exception.BadRequestException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.member.dto.MemberRequest;
import com.project.PJA.member.dto.MemberResponse;
import com.project.PJA.project_progress.dto.WorkspaceMemberDto;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.enumeration.WorkspaceRole;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import com.project.PJA.workspace_activity.enumeration.ActivityActionType;
import com.project.PJA.workspace_activity.enumeration.ActivityTargetType;
import com.project.PJA.workspace_activity.service.WorkspaceActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final WorkspaceService workspaceService;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceActivityService workspaceActivityService;
    private final RedisTemplate<String, String> redisTemplate;

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
                        member.getUser().getProfileImage(),
                        member.getWorkspaceRole(),
                        member.getJoinedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public MemberResponse updateMember(Long userId, Long workspaceId, MemberRequest memberRequest) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        Users foundUser = userRepository.findById(memberRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("요청하신 사용자를 찾을 수 없습니다."));

        WorkspaceMember targetMember = workspaceMemberRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, memberRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스와 사용자를 찾을 수 없습니다."));

        // 권한 체크: 요청자가 워크스페이스 오너인지 확인
        workspaceService.authorizeOwnerOrThrow(userId, foundWorkspace, "워크스페이스 소유자만 멤버 역할을 수정할 수 있습니다.");

        // 오너가 자기 역할을 바꾸려는 경우 차단
        if (userId.equals(memberRequest.getUserId())) {
            throw new BadRequestException("오너는 자신의 역할을 변경할 수 없습니다.");
        }

        // 이미 같은 역할이면 변경 불필요
        if (memberRequest.getWorkspaceRole() == targetMember.getWorkspaceRole()) {
            throw new BadRequestException("이미 해당 역할이 지정되어 있습니다.");
        }

        // 오너 역할 위임 처리
        if (memberRequest.getWorkspaceRole() == WorkspaceRole.OWNER) {
            // 워크스페이스 오너 변경
           foundWorkspace.updateOwner(foundUser);

            // 기존 오너 멤버로 강등
            WorkspaceMember currentOwnerMember = workspaceMemberRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, userId)
                    .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스와 사용자를 찾을 수 없습니다."));
            currentOwnerMember.update(WorkspaceRole.MEMBER);

            // 타겟 멤버 역할 OWNER로 변경
            targetMember.update(WorkspaceRole.OWNER);
        }
        // 일반 역할 변경
        else {
            targetMember.update(memberRequest.getWorkspaceRole());
        }

        // 최근 활동 기록 추가
        workspaceActivityService.addWorkspaceActivity(targetMember.getUser(), workspaceId, ActivityTargetType.ROLE, ActivityActionType.CHANGE);

        invalidateWorkspaceAuthCache(workspaceId);

        return new MemberResponse(
                targetMember.getUser().getUserId(),
                targetMember.getUser().getName(),
                targetMember.getUser().getEmail(),
                targetMember.getUser().getProfileImage(),
                memberRequest.getWorkspaceRole(),
                targetMember.getJoinedAt()
        );
    }

    // 팀원 삭제
    @Transactional
    public MemberResponse deleteMember(Long userId, Long workspaceId, Long memberId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        WorkspaceMember foundWorkspaceMember = workspaceMemberRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, memberId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스와 사용자를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrThrow(userId, foundWorkspace, "멤버를 삭제할 권한이 없습니다.");

        // 최근 활동 기록 추가
        workspaceActivityService.addWorkspaceActivity(foundWorkspaceMember.getUser(), workspaceId, ActivityTargetType.MEMBER, ActivityActionType.LEAVE);

        workspaceMemberRepository.delete(foundWorkspaceMember);

        invalidateWorkspaceAuthCache(workspaceId);

        return new MemberResponse(
                foundWorkspaceMember.getUser().getUserId(),
                foundWorkspaceMember.getUser().getName(),
                foundWorkspaceMember.getUser().getEmail(),
                foundWorkspaceMember.getUser().getProfileImage(),
                foundWorkspaceMember.getWorkspaceRole(),
                foundWorkspaceMember.getJoinedAt()
        );
    }

    // GUEST 제외한 멤버 리스트 가져오기
    public Set<WorkspaceMemberDto> getMemberWithoutGuest(Long workspaceId) {
        Set<WorkspaceMemberDto> dtoSet = new HashSet<>();
        Set<WorkspaceMember> memberSet = workspaceMemberRepository.findAllByWorkspace_WorkspaceIdAndWorkspaceRoleNot(workspaceId, WorkspaceRole.GUEST);

        for(WorkspaceMember member : memberSet) {
            WorkspaceMemberDto dto = new WorkspaceMemberDto();
            dto.setMemberId(member.getWorkspaceMemberId());
            dto.setUsername(member.getUser().getName());
            dto.setProfileImage(member.getUser().getProfileImage());
            dto.setRole(member.getWorkspaceRole());

            dtoSet.add(dto);
        }
        return dtoSet;
    }

    private void invalidateWorkspaceAuthCache(Long workspaceId) {
        redisTemplate.delete("workspaceAuth:" + workspaceId);
    }
}
