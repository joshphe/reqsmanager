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
    private Boolean importantRequirement = false;

    @Column(columnDefinition = "BIT(1) COMMENT '是否递交概要设计'")
    private Boolean summaryDesignSubmitted = false;

    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '概要设计递交人'")
    private String summaryDesignSubmitter;

    @Column(columnDefinition = "DATE COMMENT '概要设计递交日期'")
    private LocalDate summaryDesignSubmitDate;

    @Column(columnDefinition = "DATE COMMENT '概要设计评审通过日期'")
    private LocalDate summaryDesignReviewPassDate;

    @Column(columnDefinition = "BIT(1) COMMENT '是否递交详细设计'")
    private Boolean detailedDesignSubmitted = false;

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

    // === START: 核心修改 ===
    /**
     * 与评审信息建立一对一关联。
     * CascadeType.ALL: 级联所有操作。
     * orphanRemoval = true: 当解除关联时，自动删除孤立的 ReviewInfo 记录。
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "review_info_id", referencedColumnName = "id")
    private ReviewInfo reviewInfo;

    /**
     * 评审与检核信息是否一致。
     * 这个值由后端业务逻辑在保存时自动计算和设置。
     */
    @Column(columnDefinition = "BIT(1) DEFAULT b'1' COMMENT '评审检核是否一致'")
    private Boolean areReviewsConsistent = true;
}
