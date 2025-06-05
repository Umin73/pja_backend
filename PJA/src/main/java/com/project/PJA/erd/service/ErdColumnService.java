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

    public ErdColumnService(ErdTableRepository erdTableRepository, ErdColumnRepository erdColumnRepository, ErdRepository erdRepository) {
        this.erdTableRepository = erdTableRepository;
        this.erdColumnRepository = erdColumnRepository;
        this.erdRepository = erdRepository;
    }

    @Transactional
    public ErdColumn createErdColumn(Users user, Long workspaceId, Long erdId, Long tableId, ErdColumnRequestDto dto) {
        // GUEST는 생성X
        // 멤버 권한 로직 작성 완료 시 추가 필요

        Optional<Erd> optionalErd = erdRepository.findById(erdId);
        if (optionalErd.isEmpty()) {
            throw new NotFoundException("해당 ERD를 찾을 수 없습니다.");
        }
        Erd erd = optionalErd.get();

        Optional<ErdTable> optionalErdTable = erdTableRepository.findById(tableId);
        if(optionalErdTable.isEmpty()) {
            throw new NotFoundException("해당 ERD 테이블을 찾을 수 없습니다.");
        }
        ErdTable erdTable = optionalErdTable.get();

        if(!erdTable.getErd().getErdId().equals(erd.getErdId())) {
            throw new IllegalArgumentException("해당 ERD에 속하지 않은 테이블입니다.");
        }

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
    public ErdColumn updateErdColumn(Long workspaceId, Long erdId, Long tableId, Long columnId, ErdColumnRequestDto dto) {
        // GUEST는 수정X
        // 멤버 권한 로직 작성 완료 시 추가 필요

        Optional<Erd> optionalErd = erdRepository.findById(erdId);
        if (optionalErd.isEmpty()) {
            throw new NotFoundException("해당 ERD를 찾을 수 없습니다.");
        }
        Erd erd = optionalErd.get();

        Optional<ErdTable> optionalErdTable = erdTableRepository.findById(tableId);
        if(optionalErdTable.isEmpty()) {
            throw new NotFoundException("해당 ERD 테이블을 찾을 수 없습니다.");
        }
        ErdTable erdTable = optionalErdTable.get();

        if(!erdTable.getErd().getErdId().equals(erd.getErdId())) {
            throw new IllegalArgumentException("해당 ERD에 속하지 않은 테이블입니다.");
        }

        Optional<ErdColumn> optionalErdColumn = erdColumnRepository.findById(columnId);
        if(optionalErdColumn.isEmpty()) {
            throw new NotFoundException("해당 ERD 컬럼을 찾을 수 없습니다.");
        }
        ErdColumn erdColumn = optionalErdColumn.get();

        if(!erdColumn.getErdTable().getErdTableId().equals(erdTable.getErdTableId())) {
            throw new IllegalArgumentException("해당 ERD 테이블에 속하지 않은 컬럼입니다.");
        }

        erdColumn.setName(dto.getColumnName());
        erdColumn.setDataType(dto.getDataType());
        erdColumn.setNullable(dto.isNullable());
        erdColumn.setPrimaryKey(dto.isPrimaryKey());
        erdColumn.setForeignKey(dto.isForeignKey());
        return erdColumnRepository.save(erdColumn);
    }

    @Transactional
    public void deleteErdColumn(Long workspaceId, Long erdId, Long tableId, Long columnId) {
        // GUEST는 수정X
        // 멤버 권한 로직 작성 완료 시 추가 필요

        Optional<Erd> optionalErd = erdRepository.findById(erdId);
        if (optionalErd.isEmpty()) {
            throw new NotFoundException("해당 ERD를 찾을 수 없습니다.");
        }
        Erd erd = optionalErd.get();

        Optional<ErdTable> optionalErdTable = erdTableRepository.findById(tableId);
        if(optionalErdTable.isEmpty()) {
            throw new NotFoundException("해당 ERD 테이블을 찾을 수 없습니다.");
        }
        ErdTable erdTable = optionalErdTable.get();

        if(!erdTable.getErd().getErdId().equals(erd.getErdId())) {
            throw new IllegalArgumentException("해당 ERD에 속하지 않은 테이블입니다.");
        }

        Optional<ErdColumn> optionalErdColumn = erdColumnRepository.findById(columnId);
        if(optionalErdColumn.isEmpty()) {
            throw new NotFoundException("해당 ERD 컬럼을 찾을 수 없습니다.");
        }
        ErdColumn erdColumn = optionalErdColumn.get();

        if(!erdColumn.getErdTable().getErdTableId().equals(erdTable.getErdTableId())) {
            throw new IllegalArgumentException("해당 ERD 테이블에 속하지 않은 컬럼입니다.");
        }

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
}
