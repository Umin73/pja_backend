package com.project.PJA.ideainput.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.ideainput.dto.IdeaInputRequest;
import com.project.PJA.ideainput.dto.IdeaInputResponse;
import com.project.PJA.ideainput.dto.MainFunctionData;
import com.project.PJA.ideainput.dto.TechStackData;
import com.project.PJA.ideainput.service.IdeaInputService;
import com.project.PJA.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class IdeaInputController {
    private final IdeaInputService ideaInputService;

    // 아이디어 입력 조회
    @GetMapping("/{workspaceId}/idea-input")
    public ResponseEntity<SuccessResponse<IdeaInputResponse>> getIdeaInput(@AuthenticationPrincipal Users user,
                                                                           @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        log.info("=== 아이디어 입력 조회 API 진입 == userId: {}", userId);

        IdeaInputResponse ideaInputResponse = ideaInputService.getIdeaInput(userId, workspaceId);

        SuccessResponse<IdeaInputResponse> response = new SuccessResponse<>(
                "success", "아이디어 입력을 성공적으로 조회했습니다.", ideaInputResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 아이디어 입력 초기 생성
    @PostMapping("/{workspaceId}/idea-input")
    public ResponseEntity<SuccessResponse<IdeaInputResponse>> createIdeaInput(@AuthenticationPrincipal Users user,
                                                                              @PathVariable Long workspaceId) {
        Long userId = user.getUserId();
        log.info("=== 아이디어 입력 초기 생성 API 진입 == userId: {}", userId);
        
        IdeaInputResponse ideaInputResponse = ideaInputService.createIdeaInput(userId, workspaceId);

        SuccessResponse<IdeaInputResponse> response = new SuccessResponse<>(
                "success", "아이디어 입력을 성공적으로 생성했습니다.", ideaInputResponse
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // 메인 기능 생성
    @PostMapping("/{workspaceId}/idea-input/{ideaInputId}/main-function")
    public ResponseEntity<SuccessResponse<MainFunctionData>> createMainFunction(@AuthenticationPrincipal Users user,
                                                                                @PathVariable Long workspaceId,
                                                                                @PathVariable Long ideaInputId) {
        Long userId = user.getUserId();
        log.info("=== 메인 기능 생성 API 진입 == userId: {}", userId);
        
        MainFunctionData mainFunction = ideaInputService.createMainFunction(userId, workspaceId, ideaInputId);

        SuccessResponse<MainFunctionData> response = new SuccessResponse<>(
                "success", "메인 기능을 성공적으로 생성했습니다.", mainFunction
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 메인 기능 삭제
    @DeleteMapping("/{workspaceId}/idea-input/main-function/{mainFunctionId}")
    public ResponseEntity<SuccessResponse<MainFunctionData>> deleteMainFunction(@AuthenticationPrincipal Users user,
                                                                                @PathVariable Long workspaceId,
                                                                                @PathVariable Long mainFunctionId) {
        Long userId = user.getUserId();
        log.info("=== 메인 기능 삭제 API 진입 == userId: {}", userId);
        
        MainFunctionData mainFunction = ideaInputService.deleteMainFunction(userId, workspaceId, mainFunctionId);

        SuccessResponse<MainFunctionData> response = new SuccessResponse<>(
                "success", "메인 기능을 성공적으로 삭제했습니다.", mainFunction
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // 기술 스택 생성
    @PostMapping("/{workspaceId}/idea-input/{ideaInputId}/tech-stack")
    public ResponseEntity<SuccessResponse<TechStackData>> createTechStack(@AuthenticationPrincipal Users user,
                                                                          @PathVariable Long workspaceId,
                                                                          @PathVariable Long ideaInputId) {
        Long userId = user.getUserId();
        log.info("=== 기술 스택 생성 API 진입 == userId: {}", userId);
        
        TechStackData techStack = ideaInputService.createTechStack(userId, workspaceId, ideaInputId);

        SuccessResponse<TechStackData> response = new SuccessResponse<>(
                "success", "기술 스택을 성공적으로 생성했습니다.", techStack
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 기술 스택 삭제
    @DeleteMapping("/{workspaceId}/idea-input/tech-stack/{techStackId}")
    public ResponseEntity<SuccessResponse<TechStackData>> deleteTechStack(@AuthenticationPrincipal Users user,
                                                                          @PathVariable Long workspaceId,
                                                                          @PathVariable Long techStackId) {
        Long userId = user.getUserId();
        log.info("=== 기술 스택 삭제 API 진입 == userId: {}", userId);

        TechStackData techStack = ideaInputService.deleteTechStack(userId, workspaceId, techStackId);

        SuccessResponse<TechStackData> response = new SuccessResponse<>(
                "success", "기술 스택을 성공적으로 삭제했습니다.", techStack
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 아이디어 입력 수정
    @PutMapping("/{workspaceId}/idea-input/{ideaInputId}")
    public ResponseEntity<SuccessResponse<IdeaInputResponse>> updateOnlyIdeaInput(@AuthenticationPrincipal Users user,
                                                                                  @PathVariable Long workspaceId,
                                                                                  @PathVariable Long ideaInputId,
                                                                                  @RequestBody IdeaInputRequest ideaInputRequest) {
        Long userId = user.getUserId();
        log.info("=== 아이디어 입력 수정 API 진입 == userId: {}", userId);
        
        IdeaInputResponse ideaInputResponse = ideaInputService.updateIdeaInput(userId, workspaceId, ideaInputId, ideaInputRequest);

        SuccessResponse<IdeaInputResponse> response = new SuccessResponse<>(
                "success", "아이디어 입력을 성공적으로 수정했습니다.", ideaInputResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
