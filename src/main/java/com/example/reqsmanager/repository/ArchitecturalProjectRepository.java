package com.example.reqsmanager.repository;

import com.example.reqsmanager.entity.ArchitecturalProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArchitecturalProjectRepository extends JpaRepository<ArchitecturalProject, Integer>, JpaSpecificationExecutor<ArchitecturalProject> {
    // === START: 新增方法 ===
    /**
     * 根据项目编号检查项目是否存在。
     * @param projectNumber 项目编号
     * @return 如果存在则返回 true，否则返回 false
     */
    boolean existsByProjectNumber(String projectNumber);
    // === END ===

}
