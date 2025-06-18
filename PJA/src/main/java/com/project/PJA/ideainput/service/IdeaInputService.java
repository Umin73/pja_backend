package com.project.PJA.ideainput.service;

import com.project.PJA.exception.BadRequestException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.ideainput.dto.*;
import com.project.PJA.ideainput.entity.IdeaInput;
import com.project.PJA.ideainput.entity.MainFunction;
import com.project.PJA.ideainput.entity.TechStack;
import com.project.PJA.ideainput.repository.IdeaInputRepository;
import com.project.PJA.ideainput.repository.MainFunctionRepository;
import com.project.PJA.ideainput.repository.TechStackRepository;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdeaInputService {
    private final WorkspaceService workspaceService;
    private final WorkspaceRepository workspaceRepository;
    private final IdeaInputRepository ideaInputRepository;
    private final MainFunctionRepository mainFunctionRepository;
    private final TechStackRepository techStackRepository;

    // 아이디어 입력 조회
    @Transactional(readOnly = true)
    public IdeaInputResponse getIdeaInput(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.validateWorkspaceAccess(userId, foundWorkspace);

        IdeaInput foundIdeaInput = ideaInputRepository.findByWorkspace_WorkspaceId(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 아이디어 입력을 찾을 수 없습니다."));
        List<MainFunction> foundMainFunctions = mainFunctionRepository.findAllByIdeaInput_IdeaInputId(foundIdeaInput.getIdeaInputId());
        List<TechStack> foundTechStacks = techStackRepository.findAllByIdeaInput_IdeaInputId(foundIdeaInput.getIdeaInputId());

        List<MainFunctionData> mainFunctionDataList = foundMainFunctions.stream()
                .map(mainFunction -> new MainFunctionData(
                        mainFunction.getMainFunctionId(),
                        mainFunction.getContent()
                ))
                .collect(Collectors.toList());

        List<TechStackData> techStackDataList = foundTechStacks.stream()
                .map(techStack -> new TechStackData(
                        techStack.getTechStackId(),
                        techStack.getContent()
                ))
                .collect(Collectors.toList());

        return new IdeaInputResponse(
                foundIdeaInput.getIdeaInputId(),
                foundIdeaInput.getProjectName(),
                foundIdeaInput.getProjectTarget(),
                mainFunctionDataList,
                techStackDataList,
                foundIdeaInput.getProjectDescription()
        );
    }

    // 아이디어 입력 초기 생성
    @Transactional
    public IdeaInputResponse createIdeaInput(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

        if (ideaInputRepository.findByWorkspace_WorkspaceId(workspaceId).isPresent()) {
            throw new BadRequestException("해당 워크스페이스에는 이미 아이디어 입력이 존재합니다.");
        }

        IdeaInput savedIdeaInput = ideaInputRepository.save(
                IdeaInput.builder()
                        .workspace(foundWorkspace)
                        .projectName("")
                        .projectTarget("")
                        .projectDescription("")
                        .build()
        );

        MainFunction savedMainFunction1 = mainFunctionRepository.save(
                MainFunction.builder().ideaInput(savedIdeaInput).content("").build()
        );

        MainFunction savedMainFunction2 = mainFunctionRepository.save(
                MainFunction.builder().ideaInput(savedIdeaInput).content("").build()
        );

        TechStack savedTechStack1 = techStackRepository.save(
                TechStack.builder().ideaInput(savedIdeaInput).content("").build()
        );

        TechStack savedTechStack2 = techStackRepository.save(
                TechStack.builder().ideaInput(savedIdeaInput).content("").build()
        );

        List<MainFunctionData> mainFunctionList = Arrays.asList(savedMainFunction1, savedMainFunction2).stream()
                .map(mainFunction -> new MainFunctionData(
                        mainFunction.getMainFunctionId(),
                        mainFunction.getContent()))
                .collect(Collectors.toList());

        List<TechStackData> techStackList = Arrays.asList(savedTechStack1, savedTechStack2).stream()
                .map(techStack -> new TechStackData(
                        techStack.getTechStackId(),
                        techStack.getContent()))
                .collect(Collectors.toList());

        return new IdeaInputResponse(
                savedIdeaInput.getIdeaInputId(),
                savedIdeaInput.getProjectName(),
                savedIdeaInput.getProjectTarget(),
                mainFunctionList,
                techStackList,
                savedIdeaInput.getProjectDescription()
        );
    }
    
    // 메인 기능 생성
    @Transactional
    public MainFunctionData createMainFunction(Long userId, Long workspaceId, Long ideaInputId) {
        IdeaInput foundIdeaInput = ideaInputRepository.findById(ideaInputId)
                .orElseThrow(() -> new NotFoundException("요청하신 아이디어 입력을 찾을 수 없습니다."));
        
        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

        MainFunction savedMainFunction = mainFunctionRepository.save(
                MainFunction.builder().ideaInput(foundIdeaInput).content("").build()
        );

        return new MainFunctionData(
                savedMainFunction.getMainFunctionId(),
                savedMainFunction.getContent()
        );
    }

    // 메인 기능 삭제
    @Transactional
    public MainFunctionData deleteMainFunction(Long userId, Long workspaceId, Long mainFunctionId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 삭제할 권한이 없습니다.");

        MainFunction foundMainFunction = mainFunctionRepository.findById(mainFunctionId)
                .orElseThrow(() -> new NotFoundException("요청하신 메인 기능을 찾을 수 없습니다."));

        mainFunctionRepository.delete(foundMainFunction);

        return new MainFunctionData(
                mainFunctionId,
                foundMainFunction.getContent()
        );
    }
    
    // 기술 스택 생성
    @Transactional
    public TechStackData createTechStack(Long userId, Long workspaceId, Long ideaInputId) {
        IdeaInput foundIdeaInput = ideaInputRepository.findById(ideaInputId)
                .orElseThrow(() -> new NotFoundException("요청하신 아이디어 입력을 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

        TechStack savedTechStack = techStackRepository.save(
                TechStack.builder().ideaInput(foundIdeaInput).content("").build()
        );

        return new TechStackData(
                savedTechStack.getTechStackId(),
                savedTechStack.getContent()
        );
    }

    // 기술 스택 삭제
    @Transactional
    public TechStackData deleteTechStack(Long userId, Long workspaceId, Long techStackId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 삭제할 권한이 없습니다.");

        TechStack foundTechStack = techStackRepository.findById(techStackId)
                .orElseThrow(() -> new NotFoundException("요청하신 기술 스택을 찾을 수 없습니다."));

        techStackRepository.delete(foundTechStack);

        return new TechStackData(
                techStackId,
                foundTechStack.getContent()
        );
    }

    // 아이디어 입력 수정
    @Transactional
    public IdeaInputResponse updateIdeaInput(Long userId, Long workspaceId, Long ideaInputId, IdeaInputRequest ideaInputRequest) {
        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 수정할 권한이 없습니다.");

        IdeaInput foundIdeaInput = ideaInputRepository.findById(ideaInputId)
                .orElseThrow(() -> new NotFoundException("요청하신 아이디어 입력을 찾을 수 없습니다."));

        if (ideaInputRequest.getMainFunction() == null || ideaInputRequest.getMainFunction().size() < 2) {
            throw new BadRequestException("메인 기능은 최소 2개 이상 입력해야 합니다.");
        }

        if (ideaInputRequest.getTechStack() == null || ideaInputRequest.getTechStack().size() < 2) {
            throw new BadRequestException("기술 스택은 최소 2개 이상 입력해야 합니다.");
        }

        foundIdeaInput.update(ideaInputRequest.getProjectName(), ideaInputRequest.getProjectTarget(), ideaInputRequest.getProjectDescription());

        for (MainFunctionData req : ideaInputRequest.getMainFunction()) {
            MainFunction mf = mainFunctionRepository.findById(req.getMainFunctionId())
                    .orElseThrow(() -> new NotFoundException("요청하신 ID가 " + req.getMainFunctionId() + "인 메인 기능을 찾을 수 없습니다."));
            mf.update(req.getContent());
        }

        for (TechStackData req : ideaInputRequest.getTechStack()) {
            TechStack ts = techStackRepository.findById(req.getTechStackId())
                    .orElseThrow(() -> new NotFoundException("요청하신 ID가 " + req.getTechStackId() + "인 기술 스택을 찾을 수 없습니다."));
            ts.update(req.getContent());
        }

        return new IdeaInputResponse(
                ideaInputId,
                ideaInputRequest.getProjectName(),
                ideaInputRequest.getProjectTarget(),
                ideaInputRequest.getMainFunction(),
                ideaInputRequest.getTechStack(),
                ideaInputRequest.getProjectDescription()
        );
    }
}
