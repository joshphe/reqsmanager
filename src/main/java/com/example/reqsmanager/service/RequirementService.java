package com.example.reqsmanager.service;

import com.example.reqsmanager.dto.RequirementAnalysisDTO;
import com.example.reqsmanager.dto.RequirementArchitectureDTO;
import com.example.reqsmanager.dto.RequirementGeneralDTO;
import com.example.reqsmanager.entity.Requirement;
import com.example.reqsmanager.repository.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequirementService {

    @Autowired
    private RequirementRepository requirementRepository;

    public Page<Requirement> findRequirements(String reqId, Pageable pageable) {
        Specification<Requirement> spec = (root, query, cb) -> {
            if (reqId != null && !reqId.isEmpty()) {
                return cb.like(root.get("reqId"), "%" + reqId + "%");
            }
            return cb.conjunction();
        };
        return requirementRepository.findAll(spec, pageable);
    }

    public Optional<Requirement> findById(Integer id) {
        return requirementRepository.findById(id);
    }

    public void deleteById(Integer id) {
        requirementRepository.deleteById(id);
    }

    public long getTotalRequirements() {
        return requirementRepository.count();
    }

    // === START: 新增获取全量数据的方法 ===
    public List<Requirement> findAll() {
        return requirementRepository.findAll();
    }
    // === END: 新增 ===

    // === 已修正: 新增时保存所有通用信息 ===
    public Requirement createNewRequirement(RequirementGeneralDTO dto) {
        Requirement req = new Requirement();
        // 设置所有来自 General DTO 的字段
        req.setReqId(dto.getReqId());
        req.setName(dto.getName());
        req.setBusinessLeader(dto.getBusinessLeader());
        req.setTechLeader(dto.getTechLeader());
        req.setBusinessLine(dto.getBusinessLine());
        req.setDevLeader(dto.getDevLeader());
        req.setScheduleDate(dto.getScheduleDate());
        return requirementRepository.save(req);
    }

    // === 已修正: 确保所有字段都被更新 ===
    public Requirement saveGeneralInfo(RequirementGeneralDTO dto) {
        Requirement requirement = requirementRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid requirement Id:" + dto.getId()));

        requirement.setBusinessLeader(dto.getBusinessLeader());
        requirement.setTechLeader(dto.getTechLeader());
        requirement.setBusinessLine(dto.getBusinessLine());
        requirement.setDevLeader(dto.getDevLeader());
        requirement.setScheduleDate(dto.getScheduleDate());

        return requirementRepository.save(requirement);
    }

    // === 已修正: 确保所有字段都被更新 ===
    public Requirement saveAnalysisInfo(RequirementAnalysisDTO dto) {
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

    // === 已修正: 确保所有字段都被更新 ===
    public Requirement saveArchitectureInfo(RequirementArchitectureDTO dto) {
        Requirement requirement = requirementRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid requirement Id:" + dto.getId()));

        requirement.setHasArchPlan(dto.getHasArchPlan());
        requirement.setArchPlanDeliveryDate(dto.getArchPlanDeliveryDate());
        requirement.setArchPlanReviewTime(dto.getArchPlanReviewTime());
        requirement.setDesignDeliveryDate(dto.getDesignDeliveryDate());
        requirement.setDesignReviewTime(dto.getDesignReviewTime());
        requirement.setCodeReviewTime(dto.getCodeReviewTime());

        return requirementRepository.save(requirement);
    }
}
