package com.example.reqsmanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "architectural_projects")
public class ArchitecturalProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '项目编号'")
    private String projectNumber;

    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '需求编号'")
    private String reqId;

    @Column(length = 200, columnDefinition = "VARCHAR(200) COMMENT '需求名称'")
    private String reqName;

    // === START: 修正此处的字段命名 ===
    /**
     *  字段名从 isKeyProject 修改为 keyProject
     *  Lombok 会为其自动生成 isKeyProject() 这个 getter 方法
     */
    @Column(columnDefinition = "BIT(1) DEFAULT 0 COMMENT '是否重点项目'")
    private Boolean keyProject = false;
    // === END: 修正 ===

    // --- 可行性方案 ---
    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '可行性方案递交人'")
    private String feasibilitySubmitter;

    @Column(columnDefinition = "DATE COMMENT '可行性方案递交日期'")
    private LocalDate feasibilitySubmitDate;

    @Column(columnDefinition = "DATE COMMENT '可行性方案评审通过日期'")
    private LocalDate feasibilityReviewPassDate;

    @Column(columnDefinition = "INT COMMENT '可行性方案评分'")
    private Integer feasibilityScore;

    @Column(columnDefinition = "TEXT COMMENT '可行性方案扣分原因'")
    private String feasibilityDeductionReason;

    // --- 总体设计 ---
    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '总体设计递交人'")
    private String generalDesignSubmitter;

    @Column(columnDefinition = "DATE COMMENT '总体设计递交日期'")
    private LocalDate generalDesignSubmitDate;

    @Column(columnDefinition = "DATE COMMENT '总体设计评审通过日期'")
    private LocalDate generalDesignReviewPassDate;

    @Column(columnDefinition = "INT COMMENT '总体设计评分'")
    private Integer generalDesignScore;

    @Column(columnDefinition = "TEXT COMMENT '总体设计扣分原因'")
    private String generalDesignDeductionReason;

    // --- 详细设计 ---
    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '详细设计递交人'")
    private String detailedDesignSubmitter;

    @Column(columnDefinition = "DATE COMMENT '详细设计递交日期'")
    private LocalDate detailedDesignSubmitDate;

    @Column(columnDefinition = "INT COMMENT '详细设计评分'")
    private Integer detailedDesignScore;

    @Column(columnDefinition = "TEXT COMMENT '详细设计扣分原因'")
    private String detailedDesignDeductionReason;

    // --- 备注 ---
    @Column(columnDefinition = "TEXT COMMENT '备注'")
    private String remarks;
}
