package com.example.reqsmanager.repository;

import com.example.reqsmanager.entity.ArchitecturalProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArchitecturalProjectRepository extends JpaRepository<ArchitecturalProject, Integer>, JpaSpecificationExecutor<ArchitecturalProject> {
}
