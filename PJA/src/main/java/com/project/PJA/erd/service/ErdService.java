package com.project.PJA.erd.service;

import com.project.PJA.erd.entity.Erd;
import com.project.PJA.erd.repository.ErdRepository;
import com.project.PJA.exception.ConflictException;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.service.WorkspaceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class ErdService {

    private final ErdRepository erdRepository;
    private final WorkspaceService workspaceService;

    public ErdService(ErdRepository erdRepository, WorkspaceService workspaceService) {
        this.erdRepository = erdRepository;
        this.workspaceService = workspaceService;
    }

    public Erd createErd(Users user, Long workspaceId) {
        // GUEST는 생성X
        // 멤버 권한 로직 작성 완료 시 추가 필요
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId,"게스트는 ERD를 생성할 권한이 없습니다.");

        if(erdRepository.existsById(workspaceId)) {
            throw new
                    ConflictException("해당 워크스페이스에는 이미 ERD가 존재합니다.");
        }
        Erd erd = new Erd();
        erd.setWorkspaceId(workspaceId);
        erd.setCreatedAt(LocalDateTime.now());
        erd.setTables(new ArrayList<>());

        return erdRepository.save(erd);
    }
}
