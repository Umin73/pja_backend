package com.project.PJA.project_progress.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.common.file.FileStorageService;
import com.project.PJA.common.service.S3Service;
import com.project.PJA.exception.ForbiddenException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.project_progress.dto.ActionCommentResponseDto;
import com.project.PJA.project_progress.dto.ActionContentDto;
import com.project.PJA.project_progress.dto.ActionPostFileResponseDto;
import com.project.PJA.project_progress.entity.Action;
import com.project.PJA.project_progress.entity.ActionPost;
import com.project.PJA.project_progress.entity.ActionPostFile;
import com.project.PJA.project_progress.repository.ActionPostRepository;
import com.project.PJA.project_progress.repository.ActionRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActionPostService {

    private final ActionPostRepository actionPostRepository;
    private final WorkspaceService workspaceService;
    private final ActionRepository actionRepository;
    private final FileStorageService fileStorageService;
    private final S3Service s3Service;

    @Transactional
    public void createActionPost(Action savedAction) {
        ActionPost actionPost = ActionPost.builder()
                                .action(savedAction)
                                .content("")
                                .build();
        actionPostRepository.save(actionPost);
    }

    public Long getActionPostId(Long actionId) {
        ActionPost actionPost = actionPostRepository.findById(actionId).orElseThrow(
                ()->new NotFoundException("Action을 찾을 수 없습니다.")
        );

        return actionPost.getActionPostId();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> readActionPost(Long actionId, Long actionPostId) {
        ActionPost actionPost = actionPostRepository.findById(actionPostId)
                .orElseThrow(()->new NotFoundException("액션 포스트가 존재하지 않습니다."));

        if(!actionPost.getAction().getActionId().equals(actionId)) {
            throw new ForbiddenException("액션 포스트가 해당 액션에 속하지 않습니다.");
        }

        List<ActionPostFileResponseDto> fileDtoList = actionPost.getActionPostFiles().stream()
                .map(file -> ActionPostFileResponseDto.builder()
                        .filePath(file.getFilePath())
                        .contentType(file.getContentType())
                        .build())
                .toList();

        List<ActionCommentResponseDto> commentDtoList = actionPost.getComments().stream()
                .map(comment -> ActionCommentResponseDto.builder()
                        .commentId(comment.getActionCommentId())
                        .content(comment.getContent())
                        .authorName(comment.getUser().getName()) // 또는
                        .authorId(comment.getUser().getUserId()) // 작성자의 userId
                        .updatedAt(comment.getUpdatedAt())
                        .build())
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("actionName", actionPost.getAction().getName());
        result.put("content", actionPost.getContent());
        result.put("fileList", fileDtoList);
        result.put("commentList", commentDtoList);

        return result;
    }

    @Transactional
    public Map<String, Object> updateActionPostContent(Users user, Long workspaceId, Long actionId, Long actionPostId, String content, List<MultipartFile> fileList, String removedFilePaths) throws IOException {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "프로젝트 진행 액션 포스트를 수정할 권한이 없습니다.");

        ActionPost actionPost = actionPostRepository.findById(actionPostId)
                .orElseThrow(()->new NotFoundException("액션 포스트가 존재하지 않습니다."));

        if(!actionPost.getAction().getActionId().equals(actionId)) {
            throw new ForbiddenException("액션 포스트가 해당 액션에 속하지 않습니다.");
        }

        if(content == null || content.isEmpty()) actionPost.setContent("");
        else actionPost.setContent(content);

        // 삭제 할 파일만 제거
        if (removedFilePaths != null && !removedFilePaths.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            List<String> removedList = mapper.readValue(
                    removedFilePaths,
                    new TypeReference<List<String>>() {}
            );

            for (String path : removedList) {
                s3Service.deleteFile(path);
                actionPost.getActionPostFiles().removeIf(f -> f.getFilePath().equals(path));
            }
        }

        // 새로운 파일 업로드
        if(fileList != null && !fileList.isEmpty()) {
            for (MultipartFile file : fileList) {
                String path = s3Service.uploadFile(file, "action", actionPostId);
                String type = file.getContentType();

                ActionPostFile postFile = ActionPostFile.builder()
                        .filePath(path)
                        .contentType(type)
                        .actionPost(actionPost)
                        .build();

                actionPost.getActionPostFiles().add(postFile);
            }
        }

//        if(fileList != null && !fileList.isEmpty()) {
//            for(MultipartFile file : fileList) {
//                String path = s3Service.uploadFile(file, "action", actionPostId);
//                String type = file.getContentType();
//
//                ActionPostFile postFile = ActionPostFile.builder()
//                        .filePath(path)
//                        .contentType(type)
//                        .actionPost(actionPost)
//                        .build();
//
//                actionPost.getActionPostFiles().add(postFile);
//            }
//        }

        List<ActionPostFileResponseDto> fileDtoList = actionPost.getActionPostFiles().stream()
                .map(file -> ActionPostFileResponseDto.builder()
                        .filePath(file.getFilePath())
                        .contentType(file.getContentType())
                        .build())
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("actionName", actionPost.getAction().getName());
        result.put("content", actionPost.getContent());
        result.put("fileList", fileDtoList);
        return result;
    }
}
