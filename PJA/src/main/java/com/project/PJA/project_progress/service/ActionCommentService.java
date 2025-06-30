package com.project.PJA.project_progress.service;

import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.notification.service.NotificationService;
import com.project.PJA.project_progress.dto.ActionContentDto;
import com.project.PJA.project_progress.dto.NotiFacadeDto;
import com.project.PJA.project_progress.entity.Action;
import com.project.PJA.project_progress.entity.ActionComment;
import com.project.PJA.project_progress.entity.ActionParticipant;
import com.project.PJA.project_progress.entity.ActionPost;
import com.project.PJA.project_progress.repository.ActionCommentRepository;
import com.project.PJA.project_progress.repository.ActionPostRepository;
import com.project.PJA.project_progress.repository.ActionRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.WorkspaceMember;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionCommentService {

    private final WorkspaceService workspaceService;
    private final ActionRepository actionRepository;
    private final ActionPostRepository actionPostRepository;
    private final ActionCommentRepository actionCommentRepository;
    private final NotificationService notificationService;

    // 알림 댓글 작성
    @Transactional
    public NotiFacadeDto createActionComment(Users user, Long workspaceId, Long actionId, Long actionPostId, ActionContentDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션 댓글을 작성할 권한이 없습니다.");

        ActionPost actionPost = actionPostRepository.findById(actionPostId)
                .orElseThrow(() -> new NotFoundException("액션 포스트가 존재하지 않습니다."));

        Action action = actionPost.getAction();
        if (!action.getActionId().equals(actionId)) {
            throw new ForbiddenException("액션 포스트가 해당 액션에 속하지 않습니다.");
        }

        ActionComment actionComment = new ActionComment();
        actionComment.setUser(user);
        actionComment.setActionPost(actionPost);
        actionComment.setContent(dto.getContent());
        actionComment.setUpdatedAt(LocalDateTime.now());

        actionCommentRepository.save(actionComment);

        // 해당 Action의 참여자에게 알림 보내기
        Set<WorkspaceMember> participants = action.getParticipants().stream()
                .map(ActionParticipant::getWorkspaceMember)
                .collect(Collectors.toSet());

        List<Users> receivers = participants.stream()
                .map(WorkspaceMember::getUser)
                .filter(u -> !u.getUserId().equals(user.getUserId())) // 작성자 본인은 제외
                .collect(Collectors.toList());

        log.info("receiver: {}", receivers.size());
        for (Users receiver : receivers) {
            log.info("receiver: {}", receiver);
        }

        String notiMessage = user.getUsername() + "님이 " + action.getName() + "에 댓글을 달았습니다.";

        Map<String, Object> result = new HashMap<>();
        result.put("commentId", actionComment.getActionCommentId());
        result.put("username", actionComment.getUser().getName());
        result.put("createdAt", actionComment.getUpdatedAt());
        result.put("content", actionComment.getContent());

        NotiFacadeDto notiFacadeDto = new NotiFacadeDto();
        notiFacadeDto.setResult(result);
        notiFacadeDto.setReceivers(receivers);
        notiFacadeDto.setNotiMessage(notiMessage);
        notiFacadeDto.setActionPost(actionPost);

        return notiFacadeDto;
    }

    @Transactional
    public Map<String, Object> updateActionComment(Users user, Long workspaceId, Long actionId, Long commentId, ActionContentDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션 댓글을 수정할 권한이 없습니다.");

        ActionComment comment = actionCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("댓글이 존재하지 않습니다."));

        if (!comment.getActionPost().getAction().getActionId().equals(actionId)) {
            throw new ForbiddenException("댓글이 해당 액션에 속하지 않습니다.");
        }

        // 해당 댓글의 작성자가 아니면 댓글 수정 못하게 막기
        if(!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new ForbiddenException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Map<String, Object> result = new HashMap<>();
        result.put("username", comment.getUser().getName());
        result.put("createdAt", comment.getUpdatedAt());
        result.put("content", comment.getContent());

        return result;
    }

    @Transactional
    public void deleteActionComment(Users user, Long workspaceId, Long actionId, Long commentId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션 댓글을 삭제할 권한이 없습니다.");

        ActionComment comment = actionCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("댓글이 존재하지 않습니다."));

        if(!comment.getActionPost().getAction().getActionId().equals(actionId)) {
            throw new ForbiddenException("댓글이 해당 액션에 속하지 않습니다.");
        }

        // 해당 댓글의 작성자가 아니면 댓글 수정 못하게 막기
        if(!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new ForbiddenException("댓글을 삭제할 권한이 없습니다.");
        }

        actionCommentRepository.delete(comment);
    }
}
