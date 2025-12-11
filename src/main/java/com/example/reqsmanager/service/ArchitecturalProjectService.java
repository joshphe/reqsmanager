package com.example.reqsmanager.service;

import com.example.reqsmanager.entity.ArchitecturalProject;
import com.example.reqsmanager.repository.ArchitecturalProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArchitecturalProjectService {

    @Autowired
    private ArchitecturalProjectRepository projectRepository;

    public Page<ArchitecturalProject> findProjects(String reqId, Pageable pageable) {
        Specification<ArchitecturalProject> spec = (root, query, cb) -> {
            if (reqId != null && !reqId.isEmpty()) {
                return cb.like(root.get("reqId"), "%" + reqId + "%");
            }
            return cb.conjunction();
        };
        return projectRepository.findAll(spec, pageable);
    }

    public ArchitecturalProject findById(Integer id) {
        return projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid project Id:" + id));
    }

    public void save(ArchitecturalProject project) {
        projectRepository.save(project);
    }

    public void deleteById(Integer id) {
        projectRepository.deleteById(id);
    }

    // === START: 新增获取全量数据的方法 ===
    /**
     * 获取所有架构项目记录，用于数据导出。
     * @return 包含所有 ArchitecturalProject 实体的列表
     */
    public List<ArchitecturalProject> findAll() {
        return projectRepository.findAll();
    }
    // === END: 新增 ===
}
