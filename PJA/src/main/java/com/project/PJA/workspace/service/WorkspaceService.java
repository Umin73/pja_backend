package com.project.PJA.workspace.service;

import com.project.PJA.exception.BadRequestException;
import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.workspace.dto.*;
import com.project.PJA.workspace.entity.Invitation;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.enumeration.ProgressStep;
import com.project.PJA.workspace.enumeration.WorkspaceRole;
import com.project.PJA.workspace.repository.InvitationRepository;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final InvitationRepository invitationRepository;
    private final EmailService emailService;

    // 사용자의 전체 워크스페이스 조회
    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getMyWorkspaces(Long userId) {
        // 사용자가 참여한 워크스페이스들
        List<WorkspaceMember> participatingWorkspaces = workspaceMemberRepository.findAllByUserId(userId);

        // 해당 워크스페이스들의 아이디
        List<Long> workspaceIds = participatingWorkspaces.stream()
                .map(workspace -> workspace.getWorkspace().getWorkspaceId())
                .collect(Collectors.toList());

        // 해당 워크스페이스 아이디로 워크스페이스 찾기
        List<Workspace> foundWorkspaces = workspaceRepository.findAllById(workspaceIds);

        // 워크스페이스 정보들 가져오기
        List<WorkspaceResponse> userWorkspaceList = foundWorkspaces.stream()
                .map(workspace -> new WorkspaceResponse(
                        workspace.getWorkspaceId(),
                        workspace.getProjectName(),
                        workspace.getTeamName(),
                        workspace.getUser().getUser_id(),
                        workspace.getProgressStep()))
                .collect(Collectors.toList());

        return userWorkspaceList;
    }

    // 워크스페이스 생성
    @Transactional
    public WorkspaceResponse createWorkspace(Long userId, WorkspaceCreateRequest workspaceCreateRequest) {
        if (workspaceCreateRequest.getProjectName() == null || workspaceCreateRequest.getProjectName().trim().isEmpty()) {
            throw new BadRequestException("필수 항목이 누락되어 워크스페이스를 생성할 수 없습니다.");
        }

        if (workspaceCreateRequest.getTeamName() == null || workspaceCreateRequest.getTeamName().trim().isEmpty()) {
            throw new BadRequestException("필수 항목이 누락되어 워크스페이스를 생성할 수 없습니다.");
        }

        if (workspaceCreateRequest.getIsPublic() == null) {
            throw new BadRequestException("필수 항목이 누락되어 워크스페이스를 생성할 수 없습니다.");
        }

        // 사용자 조회
        Users foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 워크스페이스 생성 및 저장
        Workspace newWorkspace = Workspace.builder()
                .user(foundUser)
                .projectName(workspaceCreateRequest.getProjectName())
                .teamName(workspaceCreateRequest.getTeamName())
                .isPublic(workspaceCreateRequest.getIsPublic())
                .build();
        Workspace savedWorkspace = workspaceRepository.save(newWorkspace);

        // 워크스페이스 멤버 생성 및 저장
        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .workspace(savedWorkspace)
                .user(foundUser)
                .workspaceRole(WorkspaceRole.OWNER)
                .build();
        workspaceMemberRepository.save(workspaceMember);

        return new WorkspaceResponse(
                savedWorkspace.getWorkspaceId(),
                savedWorkspace.getProjectName(),
                savedWorkspace.getTeamName(),
                savedWorkspace.getUser().getUser_id(),
                savedWorkspace.getProgressStep());
    }

    // 워크스페이스 수정
    @Transactional
    public WorkspaceResponse updateWorkspace(Long userId, Long workspaceId, WorkspaceUpdateRequest workspaceUpdateRequest) {
        // 워크스페이스 찾기
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자가 해당 워크스페이스의 오너가 아니면 403 반환
        if(foundWorkspace.getUser().getUser_id() != userId) {
            throw new ForbiddenException("이 워크스페이스를 수정할 권한이 없습니다.");
        }

        // 해당 워크스페이스의 오너이면 수정
        foundWorkspace.update(workspaceUpdateRequest.getProjectName(), workspaceUpdateRequest.getTeamName());

        return new WorkspaceResponse(
                foundWorkspace.getWorkspaceId(),
                foundWorkspace.getProjectName(),
                foundWorkspace.getTeamName(),
                foundWorkspace.getUser().getUser_id(),
                foundWorkspace.getProgressStep());
    }

    // 워크스페이스 진행도 완료 상태 수정
    @Transactional
    public WorkspaceResponse updateCompletionStatus(Long userId, Long workspaceId, WorkspaceProgressStep workspaceProgressStep) {
        // 워크스페이스 찾기
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자가 해당 워크스페이스의 오너가 아니면 403 반환
        if(foundWorkspace.getUser().getUser_id() != userId) {
            throw new ForbiddenException("이 워크스페이스를 수정할 권한이 없습니다.");
        }

        // 해당 워크스페이스의 오너이면 수정
        ProgressStep stepEnum = ProgressStep.fromValue(workspaceProgressStep.getProgressStep());
        foundWorkspace.updateIsCompleted(stepEnum);

        return new WorkspaceResponse(
                foundWorkspace.getWorkspaceId(),
                foundWorkspace.getProjectName(),
                foundWorkspace.getTeamName(),
                foundWorkspace.getUser().getUser_id(),
                foundWorkspace.getProgressStep());
    }

    // 워크스페이스 삭제
    @Transactional
    public WorkspaceResponse deleteWorkspace(Long userId, Long workspaceId) {
        // 워크스페이스 찾기
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자가 해당 워크스페이스의 오너가 아니면 403 반환
        if(foundWorkspace.getUser().getUser_id() != userId) {
            throw new ForbiddenException("이 워크스페이스를 수정할 권한이 없습니다.");
        }

        // 해당 워크스페이스의 오너이면 삭제
        workspaceRepository.delete(foundWorkspace);

        return new WorkspaceResponse(
                foundWorkspace.getWorkspaceId(),
                foundWorkspace.getProjectName(),
                foundWorkspace.getTeamName(),
                foundWorkspace.getUser().getUser_id(),
                foundWorkspace.getProgressStep());
    }
    
    // 워크스페이스 팀원 초대 메일
    public void sendInvitation(Long userId, Long workspaceId, WorkspaceInviteRequest workspaceInviteRequest) {
        // 워크스페이스 조회
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자가 해당 워크스페이스의 오너가 아니면 403 반환
        if(foundWorkspace.getUser().getUser_id() != userId) {
            throw new ForbiddenException("워크스페이스에 팀원을 초대할 권한이 없습니다.");
        }

        //String emailToken = UUID.randomUUID().toString();

        List<Invitation> invitations = workspaceInviteRequest.getEmails().stream()
                        .map(email -> Invitation.builder()
                                .workspace(foundWorkspace)
                                .invitedEmail(email)
                                .workspaceRole(workspaceInviteRequest.getWorkspaceRole())
                                .token(generateToken(email))
                                .build())
                .collect(Collectors.toList());

        invitationRepository.saveAll(invitations);

        for (Invitation invitation : invitations) {
            String inviteUrl = "https://{yourdomain.com}/invite/accept?token=" + invitation.getToken();
            emailService.sendInvitationEmail(invitation.getInvitedEmail(), inviteUrl);
        }
    }

    public String generateToken(String email) {
        String base = email + Instant.now().toString();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("토큰 생성 실패", e);
        }
    }
}
