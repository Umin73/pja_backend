package com.project.PJA.invitation.service;

import com.project.PJA.email.service.EmailService;
import com.project.PJA.exception.BadRequestException;
import com.project.PJA.exception.ConflictException;
import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.invitation.dto.InvitationDecisionResponse;
import com.project.PJA.invitation.dto.InvitationInfoResponse;
import com.project.PJA.invitation.entity.Invitation;
import com.project.PJA.invitation.enumeration.InvitationStatus;
import com.project.PJA.invitation.repository.InvitationRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.workspace.dto.WorkspaceInviteRequest;
import com.project.PJA.workspace.dto.WorkspaceInviteResponse;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import com.project.PJA.workspace_activity.enumeration.ActivityActionType;
import com.project.PJA.workspace_activity.enumeration.ActivityTargetType;
import com.project.PJA.workspace_activity.service.WorkspaceActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationService {
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final InvitationRepository invitationRepository;
    private final EmailService emailService;
    private final WorkspaceService workspaceService;
    private final WorkspaceActivityService workspaceActivityService;
    private final RedisTemplate<String, String> redisTemplate;

    // 워크스페이스 팀원 초대 메일 전송
    @Transactional
    public WorkspaceInviteResponse sendInvitation(Long userId, Long workspaceId, WorkspaceInviteRequest request) {
        if (request.getEmails() == null || request.getEmails().isEmpty()) {
            throw new BadRequestException("초대할 이메일 주소를 한 개 이상 입력해 주세요.");
        }

        if (request.getRole() == null) {
            throw new BadRequestException("초대할 팀원의 역할을 선택해 주세요.");
        }

        // 워크스페이스 조회
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "워크스페이스에 팀원을 초대할 권한이 없습니다.");

        List<Invitation> invitations = request.getEmails().stream()
                .map(email -> Invitation.builder()
                        .workspace(foundWorkspace)
                        .invitedEmail(email)
                        .workspaceRole(request.getRole())
                        .token(UUID.randomUUID().toString())
                        .build())
                .collect(Collectors.toList());

        invitationRepository.saveAll(invitations);

        for (Invitation invitation : invitations) {
            try {
                String inviteUrl = "https://www.pja.kr/invite?token=" + invitation.getToken();
                emailService.sendInvitationEmail(invitation.getInvitedEmail(), inviteUrl);
            } catch (Exception e) {
                log.error("초대 이메일 전송 실패: {}", invitation.getInvitedEmail(), e);
            }
        }

        return new WorkspaceInviteResponse(request.getEmails(), request.getRole());
    }

    // 초대 링크 정보 조회
    @Transactional(readOnly = true)
    public InvitationInfoResponse getInvitationInfo(Long userId, String token) {
        Invitation foundInvitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("유효하지 않은 토큰입니다."));

        if (foundInvitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ForbiddenException("초대 링크가 만료되었습니다.");
        }

        Users foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        if (!foundInvitation.getInvitedEmail().equals(foundUser.getEmail())) {
            throw new ForbiddenException("초대받은 이메일과 로그인한 사용자의 이메일이 일치하지 않습니다.");
        }

        Workspace foundWorkspace = workspaceRepository.findById(foundInvitation.getWorkspace().getWorkspaceId())
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        return new InvitationInfoResponse(
                foundWorkspace.getWorkspaceId(),
                foundWorkspace.getProjectName(),
                foundWorkspace.getTeamName(),
                foundWorkspace.getUser().getName(),
                foundInvitation.getWorkspaceRole(),
                foundInvitation.getInvitationStatus());
    }

    // 초대 수락
    @Transactional
    public InvitationDecisionResponse acceptInvitation(Users user, String token) {
        Invitation foundInvitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("유효하지 않은 초대 토큰입니다."));

        Users foundUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new BadRequestException("사용자를 찾을 수 없습니다."));

        if(!foundInvitation.getInvitedEmail().equals(foundUser.getEmail())) {
            throw new ForbiddenException("초대받은 이메일과 로그인한 사용자의 이메일이 일치하지 않습니다.");
        }

        if (foundInvitation.getInvitationStatus() == InvitationStatus.ACCEPTED) {
            throw new ConflictException("이미 초대를 수락하셨습니다.");
        }

        if (foundInvitation.getInvitationStatus() == InvitationStatus.DECLINED) {
            throw new ConflictException("이미 초대를 거절하셨습니다.");
        }

        Workspace foundWorkspace = workspaceRepository.findById(foundInvitation.getWorkspace().getWorkspaceId())
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 정상이면 수락한 상태로 저장
        foundInvitation.updateAccept(InvitationStatus.ACCEPTED, LocalDateTime.now());

        // 워크스페이스 멤버에 저장
        WorkspaceMember newWorkspaceMember = WorkspaceMember.builder()
                .workspace(foundWorkspace)
                .user(foundUser)
                .workspaceRole(foundInvitation.getWorkspaceRole())
                .build();
        workspaceMemberRepository.save(newWorkspaceMember);

        // 최근 활동 기록 추가
        workspaceActivityService.addWorkspaceActivity(foundUser, foundWorkspace.getWorkspaceId(), ActivityTargetType.MEMBER, ActivityActionType.JOIN);
        invalidateWorkspaceAuthCache(foundWorkspace.getWorkspaceId());

        return new InvitationDecisionResponse(
                foundWorkspace.getWorkspaceId(),
                foundUser.getEmail(),
                foundInvitation.getWorkspaceRole());
    }

    // 초대 거절
    @Transactional
    public InvitationDecisionResponse declineInvitation(Long userId, String token) {
        Invitation foundInvitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("유효하지 않은 초대 토큰입니다."));

        Users foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("사용자를 찾을 수 없습니다."));

        if(!foundInvitation.getInvitedEmail().equals(foundUser.getEmail())) {
            throw new ForbiddenException("초대받은 이메일과 로그인한 사용자의 이메일이 일치하지 않습니다.");
        }

        if (foundInvitation.getInvitationStatus() == InvitationStatus.ACCEPTED) {
            throw new ConflictException("이미 초대를 수락하셨습니다.");
        }

        if (foundInvitation.getInvitationStatus() == InvitationStatus.DECLINED) {
            throw new ConflictException("이미 초대를 거절하셨습니다.");
        }

        Workspace foundWorkspace = workspaceRepository.findById(foundInvitation.getWorkspace().getWorkspaceId())
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 정상이면 수락한 상태로 저장
        foundInvitation.updateDecline(InvitationStatus.DECLINED);

        return new InvitationDecisionResponse(
                foundWorkspace.getWorkspaceId(),
                foundUser.getEmail(),
                foundInvitation.getWorkspaceRole());
    }

    private void invalidateWorkspaceAuthCache(Long workspaceId) {
        redisTemplate.delete("workspaceAuth:" + workspaceId);
    }
}
