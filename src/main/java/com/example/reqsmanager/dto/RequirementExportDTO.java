package com.example.reqsmanager.dto;

import lombok.Data;
import java.time.LocalDate;

// 使用 Lombok 自动生成 getter/setter
@Data
public class RequirementExportDTO {
    // 主需求信息
    private String reqId;
    private String name;
    private String businessLeader;
    private String techLeader;
    // === START: 新增字段 ===
    private String leadDepartment;
    private String groupName;
    // === END ===
    private String ReqType;
    private String businessLine;
    private String devLeader;
    private LocalDate scheduleDate;

    // 架构需求信息
    private Boolean isImportantRequirement;
    private Boolean isSummaryDesignSubmitted;
    private String summaryDesignSubmitter;
    private LocalDate summaryDesignSubmitDate;
    private LocalDate summaryDesignReviewPassDate;
    private Boolean isDetailedDesignSubmitted;
    private String detailedDesignSubmitter;
    private LocalDate detailedDesignSubmitDate;
    private Boolean involvesArchDecision;
    private Boolean involvesInfra;
    private Boolean involvesSeniorReport;
    private Integer summaryDesignScore;
    private String summaryDesignDeductionReason;
    private Integer detailedDesignScore;
    private String detailedDesignDeductionReason;
}
