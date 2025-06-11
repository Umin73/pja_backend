package com.project.PJA.erd.repository;

import com.project.PJA.erd.entity.ErdColumn;
import com.project.PJA.erd.entity.ErdRelationships;
import com.project.PJA.erd.entity.ErdTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ErdRelationshipsRepository extends JpaRepository<ErdRelationships, Long> {

    Optional<ErdRelationships> findByForeignColumn(ErdColumn foreignColumn);

}
