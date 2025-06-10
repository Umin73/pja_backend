package com.project.PJA.project_progress.service;

import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.project_progress.dto.ActionCommentDto;
import com.project.PJA.project_progress.entity.Action;
import com.project.PJA.project_progress.entity.ActionComment;
import com.project.PJA.project_progress.entity.ActionPost;
import com.project.PJA.project_progress.repository.ActionCommentRepository;
import com.project.PJA.project_progress.repository.ActionPostRepository;
import com.project.PJA.project_progress.repository.ActionRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActionCommentService {

    private final WorkspaceService workspaceService;
    private final ActionRepository actionRepository;
    private final ActionPostRepository actionPostRepository;
    private final ActionCommentRepository actionCommentRepository;

    @Transactional
    public Map<String, Object> createActionComment(Users user, Long workspaceId, Long actionId, Long actionPostId, ActionCommentDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션 댓글을 작성할 권한이 없습니다.");

        ActionPost actionPost = actionPostRepository.findById(actionPostId)
                .orElseThrow(() -> new NotFoundException("액션 포스트가 존재하지 않습니다."));

        if (!actionPost.getAction().getActionId().equals(actionId)) {
            throw new ForbiddenException("액션 포스트가 해당 액션에 속하지 않습니다.");
        }

        ActionComment actionComment = new ActionComment();
        actionComment.setUser(user);
        actionComment.setActionPost(actionPost);
        actionComment.setContent(dto.getContent());
        actionComment.setUpdatedAt(LocalDateTime.now());

        actionCommentRepository.save(actionComment);

        Map<String, Object> result = new HashMap<>();
        result.put("username", actionComment.getUser().getName());
        result.put("createdAt", actionComment.getUpdatedAt());
        result.put("content", actionComment.getContent());

        return result;
    }

    @Transactional
    public Map<String, Object> updateActionComment(Users user, Long workspaceId, Long actionId, Long commentId, ActionCommentDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션 댓글을 수정할 권한이 없습니다.");

        ActionComment comment = actionCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("댓글이 존재하지 않습니다."));

        if (!comment.getActionPost().getAction().getActionId().equals(actionId)) {
            throw new ForbiddenException("댓글이 해당 액션에 속하지 않습니다.");
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
        actionCommentRepository.delete(comment);
    }
}
