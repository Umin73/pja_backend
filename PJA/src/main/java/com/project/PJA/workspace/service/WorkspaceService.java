package com.project.PJA.workspace.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.exception.BadRequestException;
import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.workspace.dto.*;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.enumeration.ProgressStep;
import com.project.PJA.workspace.enumeration.WorkspaceRole;
import com.project.PJA.workspace.repository.WorkspaceMemberRepository;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    //private final WorkspaceActivityRepository workspaceActivityRepository;
    //private final WorkspaceActivityService workspaceActivityService;

    // 사용자의 전체 워크스페이스 조회
    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getMyWorkspaces(Long userId) {
        // 사용자가 참여한 워크스페이스들
        List<WorkspaceMember> participatingWorkspaces = workspaceMemberRepository.findAllByUser_UserId(userId);

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
                        workspace.getIsPublic(),
                        workspace.getUser().getUserId(),
                        workspace.getProgressStep()))
                .collect(Collectors.toList());
        Collections.reverse(userWorkspaceList);

        return userWorkspaceList;
    }

    // 워크스페이스의 단일 조회
    @Transactional(readOnly = true)
    public WorkspaceDetailResponse getWorkspace(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자 확인
        validateWorkspaceAccess(userId, foundWorkspace);

        return new WorkspaceDetailResponse(
                foundWorkspace.getWorkspaceId(),
                foundWorkspace.getProjectName(),
                foundWorkspace.getTeamName(),
                foundWorkspace.getIsPublic(),
                foundWorkspace.getUser().getUserId(),
                foundWorkspace.getProgressStep(),
                foundWorkspace.getGithubUrl()
        );
    }

    // 워크스페이스 생성
    @Transactional
    public WorkspaceResponse createWorkspace(Long userId, WorkspaceCreateRequest request) {
        if (request.getProjectName() == null || request.getProjectName().trim().isEmpty()) {
            throw new BadRequestException("필수 항목이 누락되어 워크스페이스를 생성할 수 없습니다.");
        }

        if (request.getTeamName() == null || request.getTeamName().trim().isEmpty()) {
            throw new BadRequestException("필수 항목이 누락되어 워크스페이스를 생성할 수 없습니다.");
        }

        if (request.getIsPublic() == null) {
            throw new BadRequestException("필수 항목이 누락되어 워크스페이스를 생성할 수 없습니다.");
        }

        // 사용자 조회
        Users foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 워크스페이스 생성 및 저장
        Workspace newWorkspace = Workspace.builder()
                .user(foundUser)
                .projectName(request.getProjectName())
                .teamName(request.getTeamName())
                .isPublic(request.getIsPublic())
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
                savedWorkspace.getIsPublic(),
                savedWorkspace.getUser().getUserId(),
                savedWorkspace.getProgressStep());
    }

    // 워크스페이스 수정
    @Transactional
    public WorkspaceDetailResponse updateWorkspace(Users user, Long workspaceId, WorkspaceUpdateRequest request) {
        if (request.getProjectName() == null || request.getProjectName().trim().isEmpty()) {
            throw new BadRequestException("프로젝트명을 입력해 주세요.");
        }

        if (request.getTeamName() == null || request.getTeamName().trim().isEmpty()) {
            throw new BadRequestException("팀 이름을 입력해 주세요.");
        }

        if (request.getIsPublic() == null) {
            throw new BadRequestException("공개 여부를 선택해 주세요.");
        }

        // 워크스페이스 찾기
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자가 해당 워크스페이스의 오너가 아니면 403 반환
        authorizeOwnerOrThrow(user.getUserId(), foundWorkspace, "이 워크스페이스를 수정할 권한이 없습니다.");

        // 해당 워크스페이스의 오너이면 수정
        foundWorkspace.update(request.getProjectName(), request.getTeamName(), request.getIsPublic(), request.getGithubUrl());

        // 최근 활동 기록 추가
        //workspaceActivityService.addWorkspaceActivity(user, workspaceId, ActivityTargetType.WORKSPACE_SETTING, ActivityActionType.UPDATE);

        return new WorkspaceDetailResponse(
                foundWorkspace.getWorkspaceId(),
                request.getProjectName(),
                request.getTeamName(),
                request.getIsPublic(),
                foundWorkspace.getUser().getUserId(),
                foundWorkspace.getProgressStep(),
                request.getGithubUrl());
    }

    // 워크스페이스 진행도 상태 수정
    @Transactional
    public WorkspaceResponse updateWorkspaceProgressStep(Long userId, Long workspaceId, WorkspaceProgressStep workspaceProgressStep) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 수정할 권한이 없습니다.");

        ProgressStep stepEnum = ProgressStep.fromValue(workspaceProgressStep.getProgressStep());
        if (stepEnum == ProgressStep.SIX) {
            throw new BadRequestException("진행도를 '완료' 단계로 변경할 수 없습니다.");
        }
        foundWorkspace.updateProgressStep(stepEnum);

        return new WorkspaceResponse(
                foundWorkspace.getWorkspaceId(),
                foundWorkspace.getProjectName(),
                foundWorkspace.getTeamName(),
                foundWorkspace.getIsPublic(),
                foundWorkspace.getUser().getUserId(),
                foundWorkspace.getProgressStep());
    }

    // 워크스페이스 진행도 상태 완료 수정
    @Transactional
    public WorkspaceResponse updateCompletionStatus(Long userId, Long workspaceId) {
        // 워크스페이스 찾기
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자가 해당 워크스페이스의 오너가 아니면 403 반환
        authorizeOwnerOrThrow(userId, foundWorkspace, "이 워크스페이스를 수정할 권한이 없습니다.");

        // 해당 워크스페이스의 오너이면 수정
        if (foundWorkspace.getProgressStep() != ProgressStep.FIVE) {
            throw new BadRequestException("해당 워크스페이스를 완료하지 않았습니다.");
        }
        foundWorkspace.updateProgressStep(ProgressStep.SIX);

        return new WorkspaceResponse(
                foundWorkspace.getWorkspaceId(),
                foundWorkspace.getProjectName(),
                foundWorkspace.getTeamName(),
                foundWorkspace.getIsPublic(),
                foundWorkspace.getUser().getUserId(),
                foundWorkspace.getProgressStep());
    }

    // 워크스페이스 삭제
    @Transactional
    public WorkspaceResponse deleteWorkspace(Long userId, Long workspaceId) {
        // 워크스페이스 찾기
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 사용자가 해당 워크스페이스의 오너가 아니면 403 반환
        authorizeOwnerOrThrow(userId, foundWorkspace, "이 워크스페이스를 삭제할 권한이 없습니다.");

        // 최근 활동 기록들 모두 삭제
        //workspaceActivityRepository.deleteByWorkspaceId(workspaceId);

        // 해당 워크스페이스의 오너이면 삭제
        workspaceRepository.delete(foundWorkspace);

        return new WorkspaceResponse(
                foundWorkspace.getWorkspaceId(),
                foundWorkspace.getProjectName(),
                foundWorkspace.getTeamName(),
                foundWorkspace.getIsPublic(),
                foundWorkspace.getUser().getUserId(),
                foundWorkspace.getProgressStep());
    }

    // 팀 탈퇴
    @Transactional
    public WorkspaceLeaveRequest leaveWorkspace(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("해당 워크스페이스를 찾을 수 없습니다."));

        if (foundWorkspace.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("해당 워크스페이스의 오너는 탈퇴할 수 없습니다.");
        }

        WorkspaceMember foundWorkspaceMember = workspaceMemberRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, userId)
                .orElseThrow(() -> new ForbiddenException("해당 워크스페이스의 팀원이 아닙니다."));

        workspaceMemberRepository.delete(foundWorkspaceMember);

        return new WorkspaceLeaveRequest(
                workspaceId,
                foundWorkspace.getProjectName(),
                foundWorkspace.getTeamName());
    }

    // 비공개 워크스페이스의 팀원이 아니면 403 반환
    public void validateWorkspaceAccess(Long userId, Workspace workspace) {
        Long workspaceId = workspace.getWorkspaceId();

        if (!workspace.getIsPublic()) {
            boolean isMember = workspaceMemberRepository.existsByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, userId);

            if(!isMember) {
                throw new ForbiddenException("이 워크스페이스에 접근할 권한이 없습니다.");
            }
        }
    }

    // 사용자가 오너가 아니면 403 반환
    public void authorizeOwnerOrThrow(Long userId, Workspace workspace, String message) {
        if (!workspace.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException(message);
        }
    }

    // 사용자가 오너 or 멤버가 아니면 403 반환
    public void authorizeOwnerOrMemberOrThrow(Long userId, Long workspaceId, String message) {
        // 워크스페이스 멤버 찾기
        WorkspaceMember member = workspaceMemberRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, userId)
                .orElseThrow(() -> new NotFoundException("이 워크스페이스에 접근할 권한이 없습니다."));

        // 사용자가 오너 or 멤버인지 확인
        if (member.getWorkspaceRole() != WorkspaceRole.OWNER && member.getWorkspaceRole() != WorkspaceRole.MEMBER) {
            throw new ForbiddenException(message);
        }
    }

    // redis에 해당 워크스페이스의 팀원 저장
    public void cacheWorkspaceAuth(Long workspaceId) {
        String key = "workspaceAuth:" + workspaceId;

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        List<WorkspaceMember> members = workspaceMemberRepository.findAllByWorkspace_WorkspaceId(workspaceId);

        List<WorkspaceAuthCache.MemberRoles> memberRoles = members.stream()
                .map(member -> new WorkspaceAuthCache.MemberRoles(
                        member.getUser().getUserId(),
                        member.getWorkspaceRole()
                )).collect(Collectors.toList());

        WorkspaceAuthCache cacheValue = new WorkspaceAuthCache(
                workspace.getIsPublic(),
                memberRoles
        );

        try {
            // dto -> json
            String json = objectMapper.writeValueAsString(cacheValue);
            redisTemplate.opsForValue().set(key, json, Duration.ofHours(9)); // 9시간 TTL
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 실패", e);
        }
    }

    // redis로 비공개인데 팀원이 아니면 403 반환
    public void validateWorkspaceAccessFromCache(Long userId, Long workspaceId) {
        String key = "workspaceAuth:" + workspaceId;
        String data = redisTemplate.opsForValue().get(key);

        // 캐시 없으면 생성
        if (data == null) {
            cacheWorkspaceAuth(workspaceId);
            data = redisTemplate.opsForValue().get(key);
        }

        try {
            // json -> JsonNode
            JsonNode node = objectMapper.readTree(data);
            boolean isPublic = node.get("isPublic").asBoolean();

            if (!isPublic) {
                JsonNode memberRoles = node.get("memberRoles");

                boolean isMember = false;
                for (JsonNode member : memberRoles) {
                    long memberId = member.get("userId").asLong();
                    if (memberId == userId) {
                        isMember = true;
                        break;
                    }
                }

                if (!isMember) {
                    throw new ForbiddenException("이 워크스페이스에 접근할 권한이 없습니다.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("권한 캐시 파싱 실패", e);
        }
    }
    
    // redis로 사용자가 오너 or 멤버가 아니면 403 반환
    public void authorizeOwnerOrMemberOrThrowFromCache(Long userId, Long workspaceId) {
        String key = "workspaceAuth:" + workspaceId;
        String data = redisTemplate.opsForValue().get(key);

        // 캐시 없으면 생성
        if (data == null) {
            cacheWorkspaceAuth(workspaceId);
            data = redisTemplate.opsForValue().get(key);
            if (data == null) {
                throw new RuntimeException("권한 캐시 생성에 실패했습니다.");
            }
        }

        try {
            // json -> JsonNode
            JsonNode node = objectMapper.readTree(data);
            JsonNode memberRoles = node.get("memberRoles");

            boolean authorized = false;

            for (JsonNode member: memberRoles) {
                long memberId = member.get("userId").asLong();
                String roleText = member.get("workspaceRole").asText();

                if (memberId == userId) {
                    WorkspaceRole role = WorkspaceRole.valueOf(roleText);
                    if (role.isOwnerOrMember()) {
                        authorized = true;
                        break;
                    }
                }
            }

            if (!authorized) {
                throw new ForbiddenException("이 워크스페이스에 수정할 권한이 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("권한 캐시 파싱 실패", e);
        }
    }
}
