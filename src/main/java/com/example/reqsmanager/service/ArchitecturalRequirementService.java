package com.example.reqsmanager.service;

import com.example.reqsmanager.dto.ArchitecturalRequirementDTO;
import com.example.reqsmanager.entity.ArchitecturalRequirement;
import com.example.reqsmanager.entity.ReviewInfo; // 导入 ReviewInfo
import com.example.reqsmanager.repository.ArchitecturalRequirementRepository;
import com.example.reqsmanager.repository.ReviewInfoRepository; // 导入 ReviewInfoRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArchitecturalRequirementService {

    @Autowired
    private ArchitecturalRequirementRepository architecturalRequirementRepository;

    // === START: 注入 ReviewInfoRepository ===
    @Autowired
    private ReviewInfoRepository reviewInfoRepository;
    // === END ===

    @Transactional
    public ArchitecturalRequirement save(ArchitecturalRequirement archReq) {
        return architecturalRequirementRepository.save(archReq);
    }

    // === START: 彻底重构此方法 ===
    /**
     * 根据 DTO 更新 ArchitecturalRequirement 及其关联的 ReviewInfo。
     * 这是一个事务性操作，确保两张表的数据要么都成功更新，要么都不更新。
     */
    @Transactional
    public void saveFromDto(ArchitecturalRequirementDTO dto) {
        // 1. 更新 ArchitecturalRequirement
        ArchitecturalRequirement archReq = architecturalRequirementRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid architectural requirement Id: " + dto.getId()));

        archReq.setImportantRequirement(dto.getImportantRequirement());
        archReq.setSummaryDesignSubmitted(dto.getSummaryDesignSubmitted());
        archReq.setSummaryDesignSubmitter(dto.getSummaryDesignSubmitter());
        archReq.setSummaryDesignSubmitDate(dto.getSummaryDesignSubmitDate());
        archReq.setSummaryDesignReviewPassDate(dto.getSummaryDesignReviewPassDate());
        archReq.setDetailedDesignSubmitted(dto.getDetailedDesignSubmitted());
        archReq.setInvolvesArchDecision(dto.getInvolvesArchDecision());
        archReq.setInvolvesInfra(dto.getInvolvesInfra());
        archReq.setInvolvesSeniorReport(dto.getInvolvesSeniorReport());
        archReq.setDetailedDesignSubmitter(dto.getDetailedDesignSubmitter());
        archReq.setDetailedDesignSubmitDate(dto.getDetailedDesignSubmitDate());

        // === START: 补全这四个字段的保存逻辑 ===
        archReq.setSummaryDesignScore(dto.getSummaryDesignScore());
        archReq.setSummaryDesignDeductionReason(dto.getSummaryDesignDeductionReason());
        archReq.setDetailedDesignScore(dto.getDetailedDesignScore());
        archReq.setDetailedDesignDeductionReason(dto.getDetailedDesignDeductionReason());
        // === END ===

        // 2. 更新关联的 ReviewInfo
        ReviewInfo reviewInfo = reviewInfoRepository.findById(dto.getReviewInfoId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid review info Id: " + dto.getReviewInfoId()));

        // 从 DTO 复制所有评审信息字段
        reviewInfo.setReviewCheck1(dto.getReviewCheck1());
        reviewInfo.setReviewCheck2(dto.getReviewCheck2());
        reviewInfo.setReviewCheck3(dto.getReviewCheck3());
        reviewInfo.setReviewCheck4(dto.getReviewCheck4());
        reviewInfo.setReviewCheck5(dto.getReviewCheck5());
        reviewInfo.setReviewCheck6(dto.getReviewCheck6());
        reviewInfo.setReviewCheck7(dto.getReviewCheck7());
        reviewInfo.setReviewCheck8(dto.getReviewCheck8());
        reviewInfo.setReviewCheck9(dto.getReviewCheck9());
        reviewInfo.setReviewLevel(dto.getReviewLevel());

        // 从 DTO 复制所有检核信息字段
        reviewInfo.setAuditCheck1(dto.getAuditCheck1());
        reviewInfo.setAuditCheck2(dto.getAuditCheck2());
        reviewInfo.setAuditCheck3(dto.getAuditCheck3());
        reviewInfo.setAuditCheck4(dto.getAuditCheck4());
        reviewInfo.setAuditCheck5(dto.getAuditCheck5());
        reviewInfo.setAuditCheck6(dto.getAuditCheck6());
        reviewInfo.setAuditCheck7(dto.getAuditCheck7());
        reviewInfo.setAuditCheck8(dto.getAuditCheck8());
        reviewInfo.setAuditCheck9(dto.getAuditCheck9());
        reviewInfo.setAuditLevel(dto.getAuditLevel());

        // === START: 新增的比对与设置逻辑 ===
        boolean isConsistent = true;
        // 使用循环进行比对，代码更简洁
        for (int i = 1; i <= 9; i++) {
            // 使用反射动态调用 getter 方法，避免大量的 if-else
            try {
                Boolean reviewCheck = (Boolean) dto.getClass().getMethod("getReviewCheck" + i).invoke(dto);
                Boolean auditCheck = (Boolean) dto.getClass().getMethod("getAuditCheck" + i).invoke(dto);

                // 处理 null 的情况，都为 null 或都为 false 视为一致
                reviewCheck = reviewCheck != null && reviewCheck;
                auditCheck = auditCheck != null && auditCheck;

                if (!reviewCheck.equals(auditCheck)) {
                    isConsistent = false;
                    break; // 一旦发现不一致，立即跳出循环
                }
            } catch (Exception e) {
                // 反射可能抛出异常，实际项目中应进行更完善的错误处理
                e.printStackTrace();
            }
        }

        // 将比对结果设置到实体中
        archReq.setAreReviewsConsistent(isConsistent);
        // === END: 新增逻辑 ===

        // 保存两个实体
        architecturalRequirementRepository.save(archReq);
        reviewInfoRepository.save(reviewInfo);
    }
    // === END: 彻底重构 ===
}
