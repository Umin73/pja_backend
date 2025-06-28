package com.project.PJA.api.repository;

import com.project.PJA.api.entity.Api;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiRepository extends JpaRepository<Api, Long> {
    List<Api> findByWorkspace_WorkspaceId(Long workspaceId);
}
