package com.project.PJA.project_progress.service;

import com.project.PJA.notification.entity.Notification;
import com.project.PJA.notification.service.NotiAsyncService;
import com.project.PJA.notification.service.NotificationService;
import com.project.PJA.project_progress.dto.ActionContentDto;
import com.project.PJA.project_progress.dto.NotiFacadeDto;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationFacade {

    private final ActionCommentService actionCommentService;
    private final NotificationService notificationService;
    private final NotiAsyncService notiAsyncService;

    public Map<String, Object> createAndSendNotification(Users user, Long workspaceId, Long actionId, Long actionPostId, ActionContentDto dto) {
        NotiFacadeDto data = actionCommentService.createActionComment(user, workspaceId, actionId, actionPostId, dto);
        Notification savedNotification = notificationService.createNotification(data.getReceivers(), data.getNotiMessage(), data.getActionPost(), workspaceId);

        // SSE 전송
        notiAsyncService.sendNotificationAsync(savedNotification, data.getReceivers(), workspaceId);

        return data.getResult();
    }
}
