package com.example.reqsmanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "architectural_requirements")
public class ArchitecturalRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 使用 OneToOne 关联到主需求实体
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id", nullable = false)
    private Requirement requirement;

    // --- 新增的业务字段 ---

    @Column(columnDefinition = "BIT(1) COMMENT '是否重要需求'")
    private Boolean isImportantRequirement = false;

    @Column(columnDefinition = "BIT(1) COMMENT '是否递交概要设计'")
    private Boolean isSummaryDesignSubmitted = false;

    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '概要设计递交人'")
    private String summaryDesignSubmitter;

    @Column(columnDefinition = "DATE COMMENT '概要设计递交日期'")
    private LocalDate summaryDesignSubmitDate;

    @Column(columnDefinition = "DATE COMMENT '概要设计评审通过日期'")
    private LocalDate summaryDesignReviewPassDate;

    @Column(columnDefinition = "BIT(1) COMMENT '是否递交详细设计'")
    private Boolean isDetailedDesignSubmitted = false;

    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '详细设计递交人'")
    private String detailedDesignSubmitter;

    @Column(columnDefinition = "DATE COMMENT '详细设计递交日期'")
    private LocalDate detailedDesignSubmitDate;

    @Column(columnDefinition = "BIT(1) COMMENT '是否涉及架构决策'")
    private Boolean involvesArchDecision = false;

    @Column(columnDefinition = "BIT(1) COMMENT '是否涉及基础架构'")
    private Boolean involvesInfra = false;

    @Column(columnDefinition = "BIT(1) COMMENT '是否涉及高阶汇报'")
    private Boolean involvesSeniorReport = false;

    @Column(columnDefinition = "INT COMMENT '概要设计评分'")
    private Integer summaryDesignScore;

    @Column(columnDefinition = "TEXT COMMENT '概要设计扣分原因'")
    private String summaryDesignDeductionReason;

    @Column(columnDefinition = "INT COMMENT '详细设计评分'")
    private Integer detailedDesignScore;

    @Column(columnDefinition = "TEXT COMMENT '详细设计扣分原因'")
    private String detailedDesignDeductionReason;
}
