package com.example.reqsmanager.service;

import com.example.reqsmanager.dto.ArchitecturalRequirementDTO;
import com.example.reqsmanager.dto.RequirementAnalysisDTO;
import com.example.reqsmanager.dto.RequirementGeneralDTO;
import com.example.reqsmanager.dto.RequirementExportDTO;
import com.example.reqsmanager.entity.ArchitecturalRequirement;
import com.example.reqsmanager.entity.Requirement;
import com.example.reqsmanager.entity.ReviewInfo;
import com.example.reqsmanager.repository.ArchitecturalRequirementRepository;
import com.example.reqsmanager.repository.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 通用的分页查询方法。
     * 支持根据需求编号（reqId）进行模糊查询。
     * Fetch（预加载）逻辑已通过 Repository 层的 @EntityGraph 注解声明，以优化性能。
     *
     * @param reqId    用于筛选的需求编号 (可以为 null 或空)
     * @param pageable 分页信息对象 (包含页码、每页条数等)
     * @return 包含查询结果和分页信息的一个 Page<Requirement> 对象
     */
    public Page<Requirement> findRequirements(String reqId, Pageable pageable) {
        Specification<Requirement> spec = (root, query, cb) -> {
            if (reqId != null && !reqId.isEmpty()) {
                return cb.like(root.get("reqId"), "%" + reqId + "%");
            }
            // 如果没有筛选条件，返回一个空的、恒为 true 的查询条件
            return cb.conjunction();
        };
        // 调用在 Repository 中被 @EntityGraph 注解过的方法
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
        archReq.setDetailedDesignSubmitted(dto.getDetailedDesignSubmitted());
        archReq.setDetailedDesignSubmitter(dto.getDetailedDesignSubmitter());
        archReq.setDetailedDesignSubmitDate(dto.getDetailedDesignSubmitDate());
        archReq.setInvolvesArchDecision(dto.getInvolvesArchDecision());
        archReq.setInvolvesInfra(dto.getInvolvesInfra());
        archReq.setInvolvesSeniorReport(dto.getInvolvesSeniorReport());
        archReq.setSummaryDesignScore(dto.getSummaryDesignScore());
        archReq.setSummaryDesignDeductionReason(dto.getSummaryDesignDeductionReason());
        archReq.setDetailedDesignScore(dto.getDetailedDesignScore());
        archReq.setDetailedDesignDeductionReason(dto.getDetailedDesignDeductionReason());

        // 3. 保存并返回
        return architecturalRequirementRepository.save(archReq);
    }

    // ===============================================
    // ============ 用于数据导出的方法 ============
    // ===============================================

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
            dto.setIsDetailedDesignSubmitted(archReq.getDetailedDesignSubmitted());
            dto.setDetailedDesignSubmitter(archReq.getDetailedDesignSubmitter());
            dto.setDetailedDesignSubmitDate(archReq.getDetailedDesignSubmitDate());
            dto.setInvolvesArchDecision(archReq.getInvolvesArchDecision());
            dto.setInvolvesInfra(archReq.getInvolvesInfra());
            dto.setInvolvesSeniorReport(archReq.getInvolvesSeniorReport());
            dto.setSummaryDesignScore(archReq.getSummaryDesignScore());
            dto.setSummaryDesignDeductionReason(archReq.getSummaryDesignDeductionReason());
            dto.setDetailedDesignScore(archReq.getDetailedDesignScore());
            dto.setDetailedDesignDeductionReason(archReq.getDetailedDesignDeductionReason());
        }

        return dto;
    }

}
