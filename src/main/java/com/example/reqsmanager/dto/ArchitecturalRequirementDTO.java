package com.example.reqsmanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ArchitecturalRequirementDTO {
    private Integer id;
    private Integer requirementId;
    private String reqId;
    private String name;

    // === START: 修正所有布尔字段的命名，去掉 'is' 前缀 ===
    private Boolean importantRequirement;
    private Boolean summaryDesignSubmitted;
    private String summaryDesignSubmitter;
    private LocalDate summaryDesignSubmitDate;
    private LocalDate summaryDesignReviewPassDate;
//    private Boolean detailedDesignSubmitted;
    // === END: 修正 ===

    /*private String detailedDesignSubmitter;
    private LocalDate detailedDesignSubmitDate;*/

    // === START: 修正所有布尔字段的命名，去掉 'is' 前缀 ===
    private Boolean involvesArchDecision;
    private Boolean involvesInfra;
    private Boolean involvesSeniorReport;
    // === END: 修正 ===

    private Integer summaryDesignScore;
    private String summaryDesignDeductionReason;
//    private Integer detailedDesignScore;
//    private String detailedDesignDeductionReason;


    // ReviewInfo 的 ID，非常重要
    private Integer reviewInfoId;

    // 评审信息
    private Boolean reviewCheck1, reviewCheck2, reviewCheck3, reviewCheck4, reviewCheck5, reviewCheck6, reviewCheck7, reviewCheck8, reviewCheck9;
    private String reviewLevel;

    // 架构检核
    private Boolean auditCheck1, auditCheck2, auditCheck3, auditCheck4, auditCheck5, auditCheck6, auditCheck7, auditCheck8, auditCheck9;
    private String auditLevel;
}
