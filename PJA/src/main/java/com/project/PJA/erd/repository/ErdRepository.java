package com.project.PJA.erd.repository;

import com.project.PJA.erd.entity.Erd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErdRepository extends JpaRepository<Erd, Long> {
}
