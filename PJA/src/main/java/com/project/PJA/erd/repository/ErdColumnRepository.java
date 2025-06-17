package com.project.PJA.erd.repository;

import com.project.PJA.erd.entity.ErdColumn;
import com.project.PJA.erd.entity.ErdTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ErdColumnRepository extends JpaRepository<ErdColumn, Long> {

    Optional<ErdColumn> findByErdColumnIdAndErdTable(Long erdColumnId, ErdTable erdTable);
    ErdColumn findByName(String name);
}
