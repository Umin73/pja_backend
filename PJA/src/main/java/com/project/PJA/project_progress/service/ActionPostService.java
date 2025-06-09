package com.project.PJA.project_progress.service;

import com.project.PJA.common.file.FileStorageService;
import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.project_progress.dto.ActionPostDto;
import com.project.PJA.project_progress.entity.Action;
import com.project.PJA.project_progress.entity.ActionPost;
import com.project.PJA.project_progress.repository.ActionPostRepository;
import com.project.PJA.project_progress.repository.ActionRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActionPostService {

    private final ActionPostRepository actionPostRepository;
    private final WorkspaceService workspaceService;
    private final ActionRepository actionRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void createActionPost(Action savedAction) {
        ActionPost actionPost = ActionPost.builder()
                                .action(savedAction)
                                .content("")
                                .build();
        actionPostRepository.save(actionPost);
    }

    @Transactional
    public void updateActionPostContent(Users user, Long workspaceId, Long actionId, Long actionPostId, ActionPostDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션 포스트를 수정할 권한이 없습니다.");

        Action action = actionRepository.findById(actionId)
                .orElseThrow(()->new NotFoundException("액션이 존재하지 않습니다."));

        ActionPost actionPost = actionPostRepository.findById(actionPostId)
                .orElseThrow(()->new NotFoundException("액션 포스트가 존재하지 않습니다."));

        if(!actionPost.getAction().getActionId().equals(action.getActionId())) {
            throw new ForbiddenException("액션 포스트가 해당 액션에 속하지 않습니다.");
        }

        actionPost.setContent(dto.getContent());
    }
}
