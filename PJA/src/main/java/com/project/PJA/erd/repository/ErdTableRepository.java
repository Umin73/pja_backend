package com.project.PJA.erd.repository;

import com.project.PJA.erd.entity.ErdTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErdTableRepository extends JpaRepository<ErdTable, Long> {
    ErdTable findByName(String name);
}
