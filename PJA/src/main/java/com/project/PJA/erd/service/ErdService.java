package com.project.PJA.erd.service;

import com.project.PJA.erd.entity.Erd;
import com.project.PJA.erd.repository.ErdRepository;
import com.project.PJA.exception.ConflictException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class ErdService {

    private final ErdRepository erdRepository;

    public ErdService(ErdRepository erdRepository) {
        this.erdRepository = erdRepository;
    }

    public Erd createErd(Long workspaceId) {
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
