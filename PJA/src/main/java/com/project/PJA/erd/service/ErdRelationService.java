package com.project.PJA.erd.service;

import com.project.PJA.erd.dto.CreateErdRelationDto;
import com.project.PJA.erd.entity.Erd;
import com.project.PJA.erd.entity.ErdColumn;
import com.project.PJA.erd.entity.ErdRelationships;
import com.project.PJA.erd.entity.ErdTable;
import com.project.PJA.erd.repository.ErdColumnRepository;
import com.project.PJA.erd.repository.ErdRelationshipsRepository;
import com.project.PJA.erd.repository.ErdRepository;
import com.project.PJA.erd.repository.ErdTableRepository;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.service.WorkspaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ErdRelationService {

    private final WorkspaceService workspaceService;
    private final ErdRepository erdRepository;
    private final ErdTableRepository erdTableRepository;
    private final ErdRelationshipsRepository erdRelationshipsRepository;
    private final ErdColumnRepository erdColumnRepository;

    public ErdRelationService(WorkspaceService workspaceService, ErdRepository erdRepository, ErdTableRepository erdTableRepository, ErdRelationshipsRepository erdRelationshipsRepository, ErdColumnRepository erdColumnRepository) {
        this.workspaceService = workspaceService;
        this.erdRepository = erdRepository;
        this.erdTableRepository = erdTableRepository;
        this.erdRelationshipsRepository = erdRelationshipsRepository;
        this.erdColumnRepository = erdColumnRepository;
    }

    @Transactional
    public ErdRelationships createRelation(Users user, Long workspaceId, Long erdId, CreateErdRelationDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "게스트는 관계를 생성할 권한이 없습니다.");

        erdRepository.findById(erdId)
                .orElseThrow(()-> new NotFoundException("ERD가 존재하지 않습니다."));
        ErdTable fromTable = erdTableRepository.findById(dto.getFromTableId())
                .orElseThrow(() -> new NotFoundException("출발 테이블이 존재하지 않습니다."));
        ErdTable toTable = erdTableRepository.findById(dto.getToTableId())
                .orElseThrow(() -> new NotFoundException("대상 테이블이 존재하지 않습니다."));

        ErdColumn fkColumn = erdColumnRepository.findByErdColumnIdAndErdTable(dto.getForeignKeyId(), fromTable)
                .orElseThrow(() -> new NotFoundException("외래키 컬럼이 존재하지 않습니다."));

        // 기존 관계 존재 여부 확인
        Optional<ErdRelationships> relationshipsOptional
                = erdRelationshipsRepository.findByForeignColumn(fkColumn);

        ErdRelationships relationships = relationshipsOptional.orElseGet(ErdRelationships::new);
        relationships.setFromTable(fromTable);
        relationships.setToTable(toTable);
        relationships.setForeignColumn(fkColumn);
        relationships.setForeignKeyName(dto.getForeignKeyName());
        relationships.setConstraintName(dto.getConstraintName());
        relationships.setType(dto.getType());

        return erdRelationshipsRepository.save(relationships);
    }

    @Transactional
    public void deleteRelation(Users user, Long workspaceId, Long erdId, Long relationId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "게스트는 ERD 관계를 삭제할 권한이 없습니다.");

        ErdRelationships relationships = erdRelationshipsRepository.findById(relationId).orElseThrow(
                () -> new NotFoundException("해당 관계를 찾을 수 없습니다.")
        );

        if(!relationships.getFromTable().getErd().getErdId().equals(erdId)) {
            throw new IllegalArgumentException("ERD에 속하지 않은 관계입니다.");
        }

        erdRelationshipsRepository.delete(relationships);
    }
}
