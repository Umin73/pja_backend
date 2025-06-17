package com.project.PJA.notification.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.notification.dto.NotiReadResponseDto;
import com.project.PJA.notification.repository.NotificationRepository;
import com.project.PJA.notification.service.NotificationService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/workspaces/")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notifiactionRepository;

    // 알림 조회 (삭제되지 않은)
    @GetMapping("{workspaceId}/noti")
    ResponseEntity<SuccessResponse<?>> getNotification(@AuthenticationPrincipal Users user,
                                               @PathVariable("workspaceId") Long workspaceId) {
        List<NotiReadResponseDto> data = notificationService.getNotiList(user, workspaceId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "알림 조회에 성공하였습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 안 읽은 알림 있는지 반환
    @GetMapping("{workspaceId}/not-read-noti")
    ResponseEntity<SuccessResponse<?>> getNotificationNotRead(@AuthenticationPrincipal Users user,
                                                              @PathVariable("workspaceId") Long workspaceId) {
        boolean data = notificationService.getNotReadNotification(user, workspaceId);
        // 읽지 않은 알림 존재하면 true 반환

        SuccessResponse<?> response = new SuccessResponse<>("success", "읽지 않은 알림 조회에 성공하였습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 안 읽은 알림 갯수 반환
    @GetMapping("{workspaceId}/not-read-noti-count")
    ResponseEntity<SuccessResponse<?>> getNotificationNotReadCount(@AuthenticationPrincipal Users user,
                                                                   @PathVariable("workspaceId") Long workspaceId) {
        long data = notificationService.countUnreadNotification(user, workspaceId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "읽지 않은 알림 개수 조회에 성공하였습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 알림 개별 읽음 처리
    @PatchMapping("{workspaceId}/noti/{notiId}")
    ResponseEntity<SuccessResponse<?>> updateReadNotification(@AuthenticationPrincipal Users user,
                                                              @PathVariable("workspaceId") Long workspaceId,
                                                              @PathVariable("notiId") Long notiId) {

        Map<String, Object> data = notificationService.readNotification(user, workspaceId, notiId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "알림을 읽음 처리 하였습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 알림 전체 읽음 처리
    @PatchMapping("{workspaceId}/noti")
    ResponseEntity<SuccessResponse<?>> updateReadAllNotification(@AuthenticationPrincipal Users user,
                                                              @PathVariable("workspaceId") Long workspaceId) {

        notificationService.readNotificationAll(user, workspaceId);
        SuccessResponse<?> response = new SuccessResponse<>("success", "모든 알림을 읽음 처리 하였습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 알림 개별 삭제
    @DeleteMapping("{workspaceId}/noti/{notiId}")
    ResponseEntity<SuccessResponse<?>> deleteNotification(@AuthenticationPrincipal Users user,
                                                          @PathVariable("workspaceId") Long workspaceId,
                                                          @PathVariable("notiId") Long notiId){

        Map<String, Object> data = notificationService.deleteNotification(user, workspaceId, notiId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "알림을 삭제했습니다.", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 알림 전체 삭제
    @DeleteMapping("{workspaceId}/noti")
    ResponseEntity<SuccessResponse<?>> deleteAllNotification(@AuthenticationPrincipal Users user,
                                                          @PathVariable("workspaceId") Long workspaceId){

        notificationService.deleteNotificationAll(user, workspaceId);

        SuccessResponse<?> response = new SuccessResponse<>("success", "전체 알림을 삭제했습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
