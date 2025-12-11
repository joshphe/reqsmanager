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
    private Boolean detailedDesignSubmitted;
    // === END: 修正 ===

    private String detailedDesignSubmitter;
    private LocalDate detailedDesignSubmitDate;

    // === START: 修正所有布尔字段的命名，去掉 'is' 前缀 ===
    private Boolean involvesArchDecision;
    private Boolean involvesInfra;
    private Boolean involvesSeniorReport;
    // === END: 修正 ===

    private Integer summaryDesignScore;
    private String summaryDesignDeductionReason;
    private Integer detailedDesignScore;
    private String detailedDesignDeductionReason;
}
