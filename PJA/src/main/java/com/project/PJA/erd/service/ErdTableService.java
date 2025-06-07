package com.project.PJA.erd.service;

import com.project.PJA.erd.dto.ErdTableNameDto;
import com.project.PJA.erd.dto.ErdTableResponseDto;
import com.project.PJA.erd.entity.Erd;
import com.project.PJA.erd.entity.ErdTable;
import com.project.PJA.erd.repository.ErdRepository;
import com.project.PJA.erd.repository.ErdTableRepository;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.service.WorkspaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class ErdTableService {

    private final ErdRepository erdRepository;
    private final ErdTableRepository erdTableRepository;
    private final WorkspaceService workspaceService;

    public ErdTableService(ErdRepository erdRepository, ErdTableRepository erdTableRepository, WorkspaceService workspaceService) {
        this.erdRepository = erdRepository;
        this.erdTableRepository = erdTableRepository;
        this.workspaceService = workspaceService;
    }

    @Transactional
    public ErdTable createErdTable(Users user, Long workspaceId, Long erdId, String tableName) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId,"게스트는 ERD 테이블을 생성할 권한이 없습니다.");

        Erd erd = findErdOrThrow(erdId);

        ErdTable erdTable = new ErdTable();

        erdTable.setErd(erd);
        erdTable.setName(tableName);

        return erdTableRepository.save(erdTable);
    }

    @Transactional
    public ErdTable updateErdTableName(Users user, Long workspaceId, Long erdId, Long erdTableId, ErdTableNameDto dto) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId,"게스트는 ERD 테이블을 수정할 권한이 없습니다.");

        ErdTable erdTable = findTableAndValidateErd(erdId, erdTableId);
        erdTable.setName(dto.getNewTableName());

        return erdTableRepository.save(erdTable);
    }

    @Transactional
    public void deleteErdTable(Users user, Long workspaceId, Long erdId, Long erdTableId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId,"게스트는 ERD 테이블을 삭제할 권한이 없습니다.");

        ErdTable erdTable = findTableAndValidateErd(erdId, erdTableId);
        erdTableRepository.delete(erdTable);
    }

    public ErdTableResponseDto getErdTableDto(ErdTable erdTable) {
        ErdTableResponseDto erdTableDto = new ErdTableResponseDto();
        erdTableDto.setTableId(erdTable.getErdTableId());
        erdTableDto.setErdId(erdTable.getErd().getErdId());
        erdTableDto.setTableName(erdTable.getName());

        return erdTableDto;
    }

    private Erd findErdOrThrow(Long erdId) {
        return erdRepository.findById(erdId)
                .orElseThrow(() -> new NotFoundException("해당 ERD를 찾을 수 없습니다."));
    }

    private ErdTable findTableAndValidateErd(Long erdId, Long tableId) {
        Erd erd = findErdOrThrow(erdId);
        ErdTable table = erdTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("ERD 테이블을 찾을 수 없습니다."));

        if (!table.getErd().getErdId().equals(erd.getErdId())) {
            throw new IllegalArgumentException("해당 ERD에 속하지 않은 테이블입니다.");
        }

        return table;
    }
}
