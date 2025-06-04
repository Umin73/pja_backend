package com.project.PJA.erd.service;

import com.project.PJA.erd.entity.Erd;
import com.project.PJA.erd.entity.ErdTable;
import com.project.PJA.erd.repository.ErdRepository;
import com.project.PJA.erd.repository.ErdTableRepository;
import com.project.PJA.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ErdTableService {

    private final ErdRepository erdRepository;
    private final ErdTableRepository erdTableRepository;

    public ErdTableService(ErdRepository erdRepository, ErdTableRepository erdTableRepository) {
        this.erdRepository = erdRepository;
        this.erdTableRepository = erdTableRepository;
    }

    public ErdTable createErdTable(Long erdId, String tableName) {

        Optional<Erd> optionalErd = erdRepository.findById(erdId);
        if(optionalErd.isEmpty()) {
            throw new NotFoundException("ERD 정보가 없습니다.");
        }
        Erd erd = optionalErd.get();

        ErdTable erdTable = new ErdTable();

        erdTable.setErd(erd);
        erdTable.setName(tableName);

        return erdTableRepository.save(erdTable);
    }
}
