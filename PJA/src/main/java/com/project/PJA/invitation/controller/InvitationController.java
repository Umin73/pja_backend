package com.project.PJA.invitation.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.invitation.dto.InvitationDecisionResponse;
import com.project.PJA.invitation.dto.InvitationInfoResponse;
import com.project.PJA.invitation.service.InvitationService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;

    // 초대 링크 정보 조회
    @GetMapping
    public ResponseEntity<SuccessResponse<InvitationInfoResponse>> getInvitationInfo(@AuthenticationPrincipal Users user,
                                                                                     @RequestParam String token) {
        Long userId = user.getUserId();
        InvitationInfoResponse invitationInfo = invitationService.getInvitationInfo(userId, token);

        SuccessResponse<InvitationInfoResponse> response = new SuccessResponse<>(
                "success", "초대 링크를 성공적으로 조회했습니다.", invitationInfo
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 팀원 초대 수락
    @PatchMapping("/{token}/accept")
    public ResponseEntity<SuccessResponse<InvitationDecisionResponse>> acceptInvitation(@AuthenticationPrincipal Users user,
                                                                                        @PathVariable String token) {
        Long userId = user.getUserId();
        InvitationDecisionResponse acceptInvitation = invitationService.acceptInvitation(userId, token);

        SuccessResponse<InvitationDecisionResponse> response = new SuccessResponse<>(
                "success", "초대를 성공적으로 수락하였습니다.", acceptInvitation
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 팀원 초대 거절
    @PatchMapping("/{token}/decline")
    public ResponseEntity<SuccessResponse<InvitationDecisionResponse>> declineInvitation(@AuthenticationPrincipal Users user,
                                                                                         @PathVariable String token) {
        Long userId = user.getUserId();
        InvitationDecisionResponse declineInvitation = invitationService.declineInvitation(userId, token);

        SuccessResponse<InvitationDecisionResponse> response = new SuccessResponse<>(
                "success", "초대를 성공적으로 거절하였습니다.", declineInvitation
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
