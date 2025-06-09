package com.project.PJA.erd.service;

import com.project.PJA.erd.dto.ErdColumnRequestDto;
import com.project.PJA.erd.dto.ErdColumnResponseDto;
import com.project.PJA.erd.entity.Erd;
import com.project.PJA.erd.entity.ErdColumn;
import com.project.PJA.erd.entity.ErdTable;
import com.project.PJA.erd.repository.ErdColumnRepository;
import com.project.PJA.erd.repository.ErdRepository;
import com.project.PJA.erd.repository.ErdTableRepository;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class ErdColumnService {

    private final ErdTableRepository erdTableRepository;
    private final ErdColumnRepository erdColumnRepository;
    private final ErdRepository erdRepository;
    private final WorkspaceService workspaceService;

    public ErdColumnService(ErdTableRepository erdTableRepository, ErdColumnRepository erdColumnRepository, ErdRepository erdRepository, WorkspaceService workspaceService) {
        this.erdTableRepository = erdTableRepository;
        this.erdColumnRepository = erdColumnRepository;
        this.erdRepository = erdRepository;
        this.workspaceService = workspaceService;
    }

    @Transactional
    public ErdColumn createErdColumn(Users user, Long workspaceId, Long erdId, Long tableId, ErdColumnRequestDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId,"게스트는 ERD 컬럼을 생성할 권한이 없습니다.");

        ErdTable erdTable = findTableAndValidateErd(erdId, tableId);

        log.info("== ERD 컬럼 엔티티 생성 시작 ==");
        ErdColumn erdColumn = new ErdColumn();
        erdColumn.setErdTable(erdTable);
        erdColumn.setName(dto.getColumnName());
        erdColumn.setDataType(dto.getDataType());
        erdColumn.setNullable(dto.isNullable());
        erdColumn.setPrimaryKey(dto.isPrimaryKey());
        erdColumn.setForeignKey(dto.isForeignKey());

        return erdColumnRepository.save(erdColumn);
    }

    @Transactional
    public ErdColumn updateErdColumn(Users user, Long workspaceId, Long erdId, Long tableId, Long columnId, ErdColumnRequestDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId,"게스트는 ERD 컬럼을 생성할 권한이 없습니다.");

        ErdColumn erdColumn = findColumnAndValidateErd(erdId, tableId, columnId);

        erdColumn.setName(dto.getColumnName());
        erdColumn.setDataType(dto.getDataType());
        erdColumn.setNullable(dto.isNullable());
        erdColumn.setPrimaryKey(dto.isPrimaryKey());
        erdColumn.setForeignKey(dto.isForeignKey());
        return erdColumnRepository.save(erdColumn);
    }

    @Transactional
    public void deleteErdColumn(Users user, Long workspaceId, Long erdId, Long tableId, Long columnId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId,"게스트는 ERD 컬럼을 생성할 권한이 없습니다.");

        ErdColumn erdColumn = findColumnAndValidateErd(erdId, tableId, columnId);

        erdColumnRepository.delete(erdColumn);
    }

    public ErdColumnResponseDto getErdColumnDto(ErdColumn erdColumn) {
        ErdColumnResponseDto createdErdColumnDto = new ErdColumnResponseDto();
        createdErdColumnDto.setColumnId(erdColumn.getErdColumnId());
        createdErdColumnDto.setTableId(erdColumn.getErdTable().getErdTableId());
        createdErdColumnDto.setColumnName(erdColumn.getName());
        createdErdColumnDto.setColumnType(erdColumn.getDataType());
        createdErdColumnDto.setNullable(erdColumn.isNullable());
        createdErdColumnDto.setPrimaryKey(erdColumn.isPrimaryKey());
        createdErdColumnDto.setForeignKey(erdColumn.isForeignKey());
        return createdErdColumnDto;
    }

    private ErdTable findTableAndValidateErd(Long erdId, Long tableId) {
        Erd erd = erdRepository.findById(erdId)
                .orElseThrow(() -> new NotFoundException("해당 ERD를 찾을 수 없습니다."));
        ErdTable table = erdTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("ERD 테이블을 찾을 수 없습니다."));

        if (!table.getErd().getErdId().equals(erd.getErdId())) {
            throw new IllegalArgumentException("해당 ERD에 속하지 않은 테이블입니다.");
        }

        return table;
    }

    private ErdColumn findColumnAndValidateErd(Long erdId, Long tableId, Long columnId) {
        ErdTable table = findTableAndValidateErd(erdId, tableId);
        ErdColumn column = erdColumnRepository.findById(columnId)
                .orElseThrow(()->new NotFoundException("해당 ERD 컬럼을 찾을 수 없습니다."));

        if(!column.getErdTable().getErdTableId().equals(table.getErdTableId())) {
            throw new IllegalArgumentException("해당 ERD 테이블에 속하지 않은 컬럼입니다.");
        }

        return column;
    }
}
