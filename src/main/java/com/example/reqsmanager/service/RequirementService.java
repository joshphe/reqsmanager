package com.example.reqsmanager.service;

import com.example.reqsmanager.dto.*;
import com.example.reqsmanager.entity.ArchitecturalRequirement;
import com.example.reqsmanager.entity.Member;
import com.example.reqsmanager.entity.Requirement;
import com.example.reqsmanager.entity.ReviewInfo;
import com.example.reqsmanager.repository.ArchitecturalRequirementRepository;
import com.example.reqsmanager.repository.MemberRepository;
import com.example.reqsmanager.repository.RequirementRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 核心业务服务类。
 * 封装了所有与需求相关的业务逻辑，包括查询、创建、更新和导出。
 * 它是 Controller 和 Repository 之间的桥梁。
 */
@Service
public class RequirementService {

    @Autowired
    private RequirementRepository requirementRepository;

    @Autowired
    private ArchitecturalRequirementRepository architecturalRequirementRepository;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * 通用的分页查询方法。
     * 支持根据需求编号、需求名称、需求科技负责人，以及投产日期区间进行过滤。
     *
     * @param reqId           用于筛选的需求编号 (可以为 null 或空)
     * @param reqName         用于筛选的需求名称 (模糊匹配)
     * @param techLeader      用于筛选的需求科技负责人 (精确匹配)
     * @param startDate       投产日期起始 (包含)
     * @param endDate         投产日期终止 (包含)
     * @param isImportantRequirement 是否重要需求
     * @param isSummaryDesignSubmitted 是否递交概要设计
     * @param pageable        分页信息对象
     * @return 包含查询结果和分页信息的一个 Page<Requirement> 对象
     */
    public Page<Requirement> findRequirements(String reqId,
                                              String reqName,
                                              String techLeader,
                                              LocalDate startDate,
                                              LocalDate endDate,
                                              Boolean isImportantRequirement,
                                              Boolean isSummaryDesignSubmitted,
                                              Pageable pageable) {
        Specification<Requirement> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 需求编号 (模糊匹配)
            if (reqId != null && !reqId.isEmpty()) {
                predicates.add(cb.like(root.get("reqId"), "%" + reqId + "%"));
            }
            // 2. 需求名称 (模糊匹配)
            if (reqName != null && !reqName.isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + reqName + "%"));
            }
            // 3. 需求科技负责人 (精确匹配)
            if (techLeader != null && !techLeader.isEmpty()) {
                predicates.add(cb.equal(root.get("techLeader"), techLeader));
            }
            // === START: 修正排期筛选逻辑为区间查询 ===
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("scheduleDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("scheduleDate"), endDate));
            }
            // === END ===
            // === START: 新增筛选逻辑 ===
            // 5. 关联 ArchitecturalRequirement 进行筛选
            if (isImportantRequirement != null || isSummaryDesignSubmitted != null) {
                // 确保我们通过 LEFT JOIN 关联 archReq，因为它可能为 null
                Join<Requirement, ArchitecturalRequirement> archReqJoin = root.join("architecturalRequirement", JoinType.LEFT);

                if (isImportantRequirement != null) {
                    predicates.add(cb.equal(archReqJoin.get("importantRequirement"), isImportantRequirement));
                }
                if (isSummaryDesignSubmitted != null) {
                    predicates.add(cb.equal(archReqJoin.get("summaryDesignSubmitted"), isSummaryDesignSubmitted));
                }
            }
            // === END ===
            // 将所有条件组合
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return requirementRepository.findAll(spec, pageable);
    }

    /**
     * 根据主键 ID 查找单个需求。
     *
     * @param id 需求的数据库主键 ID
     * @return 一个包含 Requirement 的 Optional 对象，如果找不到则为空
     */
    public Optional<Requirement> findById(Integer id) {
        return requirementRepository.findById(id);
    }

    /**
     * 获取所有需求记录（主要用于数据导出）。
     *
     * @return 包含所有 Requirement 实体的列表
     */
    public List<Requirement> findAll() {
        return requirementRepository.findAll();
    }

    /**
     * 根据主键 ID 删除一个需求。
     * 由于实体间设置了级联删除，相关的 ArchitecturalRequirement 也会被一并删除。
     *
     * @param id 要删除的需求的数据库主键 ID
     */
    public void deleteById(Integer id) {
        requirementRepository.deleteById(id);
    }

    /**
     * 获取需求总数（用于主页看板）。
     *
     * @return 数据库中需求的记录总数
     */
    public long getTotalRequirements() {
        return requirementRepository.count();
    }

    /**
     * 创建一个全新的需求。
     * 这是一个事务性操作。
     * 关键逻辑：在创建主需求(Requirement)的同时，会自动创建一个空的、与之关联的架构需求(ArchitecturalRequirement)记录。
     *
     * @param dto 包含了“需求管理”页面所有字段的数据传输对象
     * @return 持久化后的 Requirement 实体
     */
    @Transactional
    public Requirement createNewRequirement(RequirementGeneralDTO dto) {
        Requirement req = new Requirement();
        req.setReqId(dto.getReqId());
        req.setName(dto.getName());
        req.setBusinessLeader(dto.getBusinessLeader());
        req.setTechLeader(dto.getTechLeader());
        // === START: 新增字段的设置 ===
        req.setLeadDepartment(dto.getLeadDepartment());
        // === START: 新增 ===
        req.setGroupName(dto.getGroupName());
        // === END ===
        req.setReqType(dto.getReqType());
        // === END: 新增 ===

        req.setBusinessLine(dto.getBusinessLine());
        req.setDevLeader(dto.getDevLeader());
        req.setScheduleDate(dto.getScheduleDate());

        // 创建一个空的、关联的架构需求对象
        ArchitecturalRequirement archReq = new ArchitecturalRequirement();
        archReq.setRequirement(req); // 建立关联：将主需求设置到架构需求中
        // === 新增：级联创建 ReviewInfo ===
        ReviewInfo reviewInfo = new ReviewInfo();
        archReq.setReviewInfo(reviewInfo);
        // reviewInfo.setArchitecturalRequirement(archReq); // 如果是双向关联，也设置反向引用
        // === 结束 ===
        req.setArchitecturalRequirement(archReq); // 建立关联：将架构需求设置到主需求中

        // 由于在 Requirement 实体中设置了 CascadeType.ALL,
        // 所以只需要保存主对象 req，JPA 会自动一并保存 archReq。
        return requirementRepository.save(req);
    }

    /**
     * 保存“需求管理”页面的基本信息。
     * 此方法只更新 Requirement 表中的通用字段。
     *
     * @param dto 包含了“需求管理”页面所有字段的数据传输对象
     * @return 更新后的 Requirement 实体
     * @throws IllegalArgumentException 如果根据ID找不到对应的需求
     */
    public Requirement saveGeneralInfo(RequirementGeneralDTO dto) {
        Requirement requirement = requirementRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid requirement Id:" + dto.getId()));

        // 注意：reqId 和 name 在编辑时通常是只读的，这里不进行更新
        requirement.setBusinessLeader(dto.getBusinessLeader());
        requirement.setTechLeader(dto.getTechLeader());
        // === START: 新增字段的设置 ===
        requirement.setLeadDepartment(dto.getLeadDepartment());
        // === START: 新增 ===
        requirement.setGroupName(dto.getGroupName());
        // === END ===
        requirement.setReqType(dto.getReqType());
        // === END: 新增 ===
        requirement.setBusinessLine(dto.getBusinessLine());
        requirement.setDevLeader(dto.getDevLeader());
        requirement.setScheduleDate(dto.getScheduleDate());

        return requirementRepository.save(requirement);
    }

    /**
     * 保存“需求分析管理”页面的信息。
     * 此方法只更新 Requirement 表中的需求分析相关字段。
     *
     * @param dto 包含了“需求分析管理”页面所有字段的数据传输对象
     * @return 更新后的 Requirement 实体
     * @throws IllegalArgumentException 如果根据ID找不到对应的需求
     */
    public Requirement saveAnalysisInfo(RequirementAnalysisDTO dto) {
        // 分析信息字段是主 Requirement 实体的一部分
        Requirement requirement = requirementRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid requirement Id:" + dto.getId()));

        requirement.setIsAnalysisInvolved(dto.getIsAnalysisInvolved());
        requirement.setAnalysisMembers(dto.getAnalysisMembers());
        requirement.setAnalysisFinishDate(dto.getAnalysisFinishDate());
        requirement.setAnalysisOutput(dto.getAnalysisOutput());
        requirement.setHasSpec(dto.getHasSpec());
        requirement.setSpecWriter(dto.getSpecWriter());
        requirement.setIsSpecReviewed(dto.getIsSpecReviewed());
        requirement.setSpecReviewTime(dto.getSpecReviewTime());

        return requirementRepository.save(requirement);
    }

    /**
     * 保存“架构需求管理”页面的信息。
     * 这是一个事务性操作。
     * 此方法只更新关联的 ArchitecturalRequirement 表。
     *
     * @param dto 包含了“架构需求管理”页面所有字段的数据传输对象
     * @return 更新后的 ArchitecturalRequirement 实体
     * @throws IllegalArgumentException 如果根据ID找不到对应的架构需求记录
     */
    @Transactional
    public ArchitecturalRequirement saveArchitecturalInfo(ArchitecturalRequirementDTO dto) {
        // 1. 根据 DTO 中的 ID 找到要更新的 ArchitecturalRequirement 记录
        ArchitecturalRequirement archReq = architecturalRequirementRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid architectural requirement Id: " + dto.getId()));

        // 2. 从 DTO 完整地复制所有可编辑字段到实体
        archReq.setImportantRequirement(dto.getImportantRequirement());
        archReq.setSummaryDesignSubmitted(dto.getSummaryDesignSubmitted());
        archReq.setSummaryDesignSubmitter(dto.getSummaryDesignSubmitter());
        archReq.setSummaryDesignSubmitDate(dto.getSummaryDesignSubmitDate());
        archReq.setSummaryDesignReviewPassDate(dto.getSummaryDesignReviewPassDate());
        archReq.setInvolvesArchDecision(dto.getInvolvesArchDecision());
        archReq.setInvolvesInfra(dto.getInvolvesInfra());
        archReq.setInvolvesSeniorReport(dto.getInvolvesSeniorReport());
        archReq.setSummaryDesignScore(dto.getSummaryDesignScore());
        archReq.setSummaryDesignDeductionReason(dto.getSummaryDesignDeductionReason());

        // 3. 保存并返回
        return architecturalRequirementRepository.save(archReq);
    }

    /**
     * 获取所有需求数据，并将其转换为用于导出的“扁平化”DTO列表。
     *
     * @return 包含所有整合后信息的 DTO 列表
     */
    public List<RequirementExportDTO> findAllForExport() {
        List<Requirement> requirements = requirementRepository.findAll();
        return requirements.stream()
                .map(this::convertToExportDto) // 为每个 Requirement 调用转换方法
                .collect(Collectors.toList());
    }

    /**
     * [私有辅助方法] 将单个 Requirement 实体转换为 RequirementExportDTO.
     * 这个方法将主表和关联的架构需求表信息“拍平”到一个对象中。
     *
     * @param req Requirement 实体
     * @return 包含了整合信息的 RequirementExportDTO 对象
     */
    private RequirementExportDTO convertToExportDto(Requirement req) {
        RequirementExportDTO dto = new RequirementExportDTO();

        // 1. 映射主需求信息
        dto.setReqId(req.getReqId());
        dto.setName(req.getName());
        dto.setBusinessLeader(req.getBusinessLeader());
        dto.setTechLeader(req.getTechLeader());
        dto.setLeadDepartment(req.getLeadDepartment());
        dto.setGroupName(req.getGroupName());
        dto.setReqType(req.getReqType());
        dto.setBusinessLine(req.getBusinessLine());
        dto.setDevLeader(req.getDevLeader());
        dto.setScheduleDate(req.getScheduleDate());

        // 2. 映射关联的架构需求信息 (进行空指针安全检查)
        ArchitecturalRequirement archReq = req.getArchitecturalRequirement();
        if (archReq != null) {
            dto.setIsImportantRequirement(archReq.getImportantRequirement());
            dto.setIsSummaryDesignSubmitted(archReq.getSummaryDesignSubmitted());
            dto.setSummaryDesignSubmitter(archReq.getSummaryDesignSubmitter());
            dto.setSummaryDesignSubmitDate(archReq.getSummaryDesignSubmitDate());
            dto.setSummaryDesignReviewPassDate(archReq.getSummaryDesignReviewPassDate());
            dto.setInvolvesArchDecision(archReq.getInvolvesArchDecision());
            dto.setInvolvesInfra(archReq.getInvolvesInfra());
            dto.setInvolvesSeniorReport(archReq.getInvolvesSeniorReport());
            dto.setSummaryDesignScore(archReq.getSummaryDesignScore());
            dto.setSummaryDesignDeductionReason(archReq.getSummaryDesignDeductionReason());
        }

        return dto;
    }

    // === START: 新增获取指标的方法 ===
    public List<GroupMetricsDTO> getGroupMetrics() {
        return requirementRepository.findGroupMetrics();
    }
    // === END ===

    // === START: 新增导入功能的核心方法 ===

    /**
     * 从上传的 CSV 文件中批量导入需求。
     *
     * @param file 用户上传的 MultipartFile
     * @return 一个包含处理结果的摘要字符串
     */
    @Transactional
    public String importFromCsv(MultipartFile file) {
        List<Requirement> newRequirements = new ArrayList<>();
        int skippedCount = 0;

        // 定义 CSV 文件中日期的格式，例如 "2023/1/1"
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            // 配置 CSV 解析器，假设文件有表头，我们将忽略它
            CSVParser csvParser = new CSVParser(fileReader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            for (CSVRecord record : csvParser) {
                // A列: 需求编号 (索引为0)
                String reqId = record.get(0);

                // 如果需求编号为空或已存在，则跳过
                if (reqId == null || reqId.isEmpty() || requirementRepository.existsByReqId(reqId)) {
                    skippedCount++;
                    continue;
                }

                Requirement req = new Requirement();
                req.setReqId(reqId);
                // B列: 需求名称
                req.setName(record.get(1));
                // D列: 需求科技负责人
                req.setTechLeader(record.get(3));
                // E列: 需求类型
                req.setReqType(record.get(4));
                // F列: 牵头部室
                req.setLeadDepartment(record.get(5));
                // === START: 核心修改，读取 H 列作为业务条线 ===
                // H 列的索引是 7
                req.setBusinessLine(record.get(7));
                // === END: 核心修改 ===
                // N列: 计划投产日期 (索引为13)
                try {
                    String dateStr = record.get(13);
                    if (dateStr != null && !dateStr.isEmpty()) {
                        req.setScheduleDate(LocalDate.parse(dateStr, dateFormatter));
                    }
                } catch (Exception e) {
                    // 日期格式错误，可以记录日志，但我们选择跳过这个字段
                    System.err.println("Skipping invalid date format for reqId: " + reqId);
                }

                // 级联创建空的关联对象
                ArchitecturalRequirement archReq = new ArchitecturalRequirement();
                archReq.setRequirement(req);
                req.setArchitecturalRequirement(archReq);

                newRequirements.add(req);
            }

            // 批量保存所有新需求，比逐条保存性能更好
            if (!newRequirements.isEmpty()) {
                requirementRepository.saveAll(newRequirements);
            }

        } catch (Exception e) {
            // 抛出运行时异常，触发事务回滚
            throw new RuntimeException("CSV 文件处理失败: " + e.getMessage());
        }

        return String.format("导入完成！新增记录: %d 条，跳过重复记录: %d 条。", newRequirements.size(), skippedCount);
    }
    // === END ===

    // === START: 新增批量删除方法 ===

    /**
     * 根据提供的 ID 列表，批量删除需求。
     * 这是一个事务性操作。
     *
     * @param ids 要删除的需求 ID 列表
     */
    @Transactional
    public void deleteByIds(List<Integer> ids) {
        // JpaRepository 提供了高效的批量删除方法
        requirementRepository.deleteAllById(ids);
    }
    // === END ===

    // === START: 新增“一键更新”的核心业务逻辑 ===

    /**
     * 批量更新需求的“所属小组”和“开发负责人”。
     * 规则：
     * 1. 遍历所有需求。
     * 2. 根据需求的“科技负责人”姓名查找对应的成员信息。
     * 3. 如果找到了成员，并且该成员有所属小组：
     * a. 如果需求的“所属小组”为空，则用成员的小组信息更新它。
     * b. 开发负责人直接更新为科技负责人的姓名。
     * 4. 批量保存所有被修改的需求。
     *
     * @return 返回一个包含处理结果的摘要字符串。
     */
    @Transactional
    public String batchUpdateFromTechLeader() {
        // 1. 获取所有需求
        List<Requirement> allRequirements = requirementRepository.findAll();
        List<Requirement> updatedRequirements = new ArrayList<>();
        int updatedCount = 0;
        int skippedCount = 0;

        for (Requirement req : allRequirements) {
            String techLeaderName = req.getTechLeader();
            boolean needsUpdate = false;

            // 2. 检查是否有科技负责人
            if (techLeaderName == null || techLeaderName.trim().isEmpty()) {
                skippedCount++;
                continue;
            }

            // 3. 根据科技负责人姓名查找成员信息
            Optional<Member> memberOpt = memberRepository.findByName(techLeaderName.trim());

            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();

                // 4. 更新“所属小组”（仅当需求的小组为空时）
                if (member.getTeamGroup() != null && (req.getGroupName() == null || req.getGroupName().trim().isEmpty())) {
                    req.setGroupName(member.getTeamGroup().getName());
                    needsUpdate = true;
                }

                // 5. 更新“开发负责人”（总是更新为科技负责人）
                if (!techLeaderName.trim().equals(req.getDevLeader())) {
                    req.setDevLeader(techLeaderName.trim());
                    needsUpdate = true;
                }

            } else {
                // 如果在成员表中找不到该负责人，则跳过
                skippedCount++;
                continue;
            }

            if (needsUpdate) {
                updatedRequirements.add(req);
                updatedCount++;
            } else {
                skippedCount++;
            }
        }

        // 6. 批量保存所有已更新的需求
        if (!updatedRequirements.isEmpty()) {
            requirementRepository.saveAll(updatedRequirements);
        }

        return String.format("一键更新完成！成功更新记录: %d 条，跳过或无需更新记录: %d 条。", updatedCount, skippedCount);
    }
    // === END ===
}
