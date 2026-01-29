package com.example.reqsmanager.service;

import com.example.reqsmanager.entity.ArchitecturalProject;
import com.example.reqsmanager.repository.ArchitecturalProjectRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.criteria.Predicate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArchitecturalProjectService {

    @Autowired
    private ArchitecturalProjectRepository projectRepository;

    /**
     * 通用的分页查询方法。
     * 支持根据项目编号、需求编号、需求名称、项目负责人、是否重点项目进行筛选。
     * @param projectNumber   项目编号 (模糊匹配)
     * @param reqId           需求编号 (模糊匹配)
     * @param reqName         需求名称 (模糊匹配)
     * @param projectManager  项目负责人 (精确匹配)
     * @param isKeyProject    是否重点项目 (精确匹配)
     * @param pageable        分页信息对象
     * @return 包含查询结果和分页信息的一个 Page<ArchitecturalProject> 对象
     */
    public Page<ArchitecturalProject> findProjects(String projectNumber, String reqId, String reqName, String projectManager, Boolean isKeyProject, Pageable pageable) {
        Specification<ArchitecturalProject> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 项目编号 (模糊匹配)
            if (projectNumber != null && !projectNumber.isEmpty()) {
                predicates.add(cb.like(root.get("projectNumber"), "%" + projectNumber + "%"));
            }
            // 2. 需求编号 (模糊匹配)
            if (reqId != null && !reqId.isEmpty()) {
                predicates.add(cb.like(root.get("reqId"), "%" + reqId + "%"));
            }
            // 3. 需求名称 (模糊匹配)
            if (reqName != null && !reqName.isEmpty()) {
                predicates.add(cb.like(root.get("reqName"), "%" + reqName + "%"));
            }
            // 4. 项目负责人 (精确匹配)
            if (projectManager != null && !projectManager.isEmpty()) {
                predicates.add(cb.equal(root.get("projectManager"), projectManager));
            }
            // 5. 是否重点项目 (精确匹配)
            if (isKeyProject != null) {
                predicates.add(cb.equal(root.get("keyProject"), isKeyProject));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
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

    // === START: 新增导入功能的核心方法 ===
    /**
     * 从上传的 CSV 文件中批量导入架构项目。
     * @param file 用户上传的 MultipartFile
     * @return 一个包含处理结果的摘要字符串
     */
    @Transactional
    public String importFromCsv(MultipartFile file) {
        List<ArchitecturalProject> newProjects = new ArrayList<>();
        int skippedCount = 0;

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .build();

            try (CSVParser csvParser = new CSVParser(fileReader, csvFormat)) {
                for (CSVRecord record : csvParser) {
                    // 0列: 项目编号
                    String projectNumber = record.get(0);

                    // 如果项目编号为空或已存在，则跳过
                    if (projectNumber == null || projectNumber.isEmpty() || projectRepository.existsByProjectNumber(projectNumber)) {
                        skippedCount++;
                        continue;
                    }

                    ArchitecturalProject project = new ArchitecturalProject();
                    project.setProjectNumber(projectNumber);
                    project.setReqId(record.get(1)); // 1列: 需求编号
                    project.setReqName(record.get(2)); // 2列: 需求名称
                    project.setDevDepartment(record.get(3)); // 3列: 开发部室
                    project.setProjectManager(record.get(4)); // 4列: 项目负责人

                    newProjects.add(project);
                }
            }

            // 批量保存所有新项目
            if (!newProjects.isEmpty()) {
                projectRepository.saveAll(newProjects);
            }

        } catch (Exception e) {
            throw new RuntimeException("CSV 文件处理失败: " + e.getMessage());
        }

        return String.format("导入完成！新增记录: %d 条，跳过重复记录: %d 条。", newProjects.size(), skippedCount);
    }
    // === END ===
}
