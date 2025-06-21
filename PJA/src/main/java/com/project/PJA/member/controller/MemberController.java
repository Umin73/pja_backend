package com.project.PJA.member.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.member.dto.MemberRequest;
import com.project.PJA.member.dto.MemberResponse;
import com.project.PJA.member.service.MemberService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class MemberController {
    private final MemberService memberService;
    
    // 팀원 전체 조회
    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<SuccessResponse<List<MemberResponse>>> getMembers(@AuthenticationPrincipal Users user,
                                                                            @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        List<MemberResponse> memberResponses = memberService.getMembers(userId, workspaceId);

        SuccessResponse<List<MemberResponse>> response = new SuccessResponse<>(
                "success", "해당 워크스페이스의 멤버 리스트를 성공적으로 조회했습니다.", memberResponses
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // 팀원 역할 변경
    @PutMapping("/{workspaceId}/members")
    public ResponseEntity<SuccessResponse<MemberResponse>> updateMember(@AuthenticationPrincipal Users user,
                                                                        @PathVariable Long workspaceId,
                                                                        @RequestBody MemberRequest memberRequest) {
        Long userId = user.getUserId();
        MemberResponse memberResponse = memberService.updateMember(user.getUserId(), workspaceId, memberRequest);

        SuccessResponse<MemberResponse> response = new SuccessResponse<>(
                "success", "해당 워크스페이스의 멤버의 역할이 성공적으로 수정되었습니다.", memberResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 팀원 삭제
    @DeleteMapping("/{workspaceId}/members/{memberId}")
    public ResponseEntity<SuccessResponse<MemberResponse>> deleteMember(@AuthenticationPrincipal Users user,
                                                                        @PathVariable Long workspaceId,
                                                                        @PathVariable Long memberId) {
        Long userId = user.getUserId();
        MemberResponse memberResponse = memberService.deleteMember(userId, workspaceId, memberId);

        SuccessResponse<MemberResponse> response = new SuccessResponse<>(
                "success", "해당 워크스페이스의 멤버가 성공적으로 삭제되었습니다.", memberResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
