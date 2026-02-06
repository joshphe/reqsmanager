package com.example.reqsmanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "requirements")
public class Requirement {

    /**
     * 主键ID, 自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 需求编号 (核心业务ID, 必须唯一)
     */
    @Column(unique = true, nullable = false, length = 50, columnDefinition = "VARCHAR(50) COMMENT '需求编号'")
    private String reqId;

    /**
     * 需求名称
     */
    @Column(nullable = false, length = 200, columnDefinition = "VARCHAR(200) COMMENT '需求名称'")
    private String name;

    // ===============================================
    // =========== 需求管理 (General) 字段 ===========
    // ===============================================

    /**
     * 需求业务负责人
     */
    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '需求业务负责人'")
    private String businessLeader;

    /**
     * 需求科技负责人
     */
    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '需求科技负责人'")
    private String techLeader;

    @Column(length = 100, columnDefinition = "VARCHAR(100) COMMENT '牵头部室'")
    private String leadDepartment;

    // === START: 新增字段 ===
    @Column(length = 100, columnDefinition = "VARCHAR(100) COMMENT '所属小组'")
    private String groupName;
    // === END: 新增字段 ===

    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '需求类型'")
    private String reqType;

    /**
     * 业务条线
     */
    @Column(length = 100, columnDefinition = "VARCHAR(100) COMMENT '业务条线'")
    private String businessLine;

    /**
     * 开发负责人
     */
    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '开发负责人'")
    private String devLeader;

    /**
     * 需求排期 (预计上线日期)
     */
    @Column(columnDefinition = "DATE COMMENT '需求排期'")
    private LocalDate scheduleDate;

    // === START: 新增字段 ===
    @Column(length = 20, columnDefinition = "VARCHAR(20) DEFAULT '进行中' COMMENT '需求状态'")
    private String status = "进行中"; // 默认值为"进行中"
    // === END: 新增字段 ===

    // ==================================================
    // =========== 需求分析 (Analysis) 字段 ===========
    // ==================================================

    /**
     * 是否需分介入 (需求分析团队是否介入)
     */
    @Column(columnDefinition = "BIT(1) COMMENT '是否需分介入'")
    private Boolean isAnalysisInvolved = false;

    /**
     * 团队需分人员 (可为多个, 逗号分隔)
     */
    @Column(length = 255, columnDefinition = "VARCHAR(255) COMMENT '团队需分人员'")
    private String analysisMembers;

    /**
     * 需分完成日期
     */
    @Column(columnDefinition = "DATE COMMENT '需分完成日期'")
    private LocalDate analysisFinishDate;

    /**
     * 需分产出物 (如: PRD, Story Map等)
     */
    @Column(length = 255, columnDefinition = "VARCHAR(255) COMMENT '需分产出物'")
    private String analysisOutput;

    /**
     * 是否产出需规 (需求规格说明书)
     */
    @Column(columnDefinition = "BIT(1) COMMENT '是否产出需规'")
    private Boolean hasSpec = false;

    /**
     * 需规编写人员
     */
    @Column(length = 50, columnDefinition = "VARCHAR(50) COMMENT '需规编写人员'")
    private String specWriter;

    /**
     * 是否需规评审
     */
    @Column(columnDefinition = "BIT(1) COMMENT '是否需规评审'")
    private Boolean isSpecReviewed = false;

    /**
     * 需规评审时间
     * 类型从 LocalDateTime 修改为 LocalDate
     */
    @Column(columnDefinition = "DATE COMMENT '需规评审时间'")
    private LocalDate specReviewTime;

    // ==================================================
    // =========== 架构管理 (Architecture) 字段 ===========
    // ==================================================

    // === 2025-12-11 START: 关键修改 ===
    /**
     * 与架构需求信息建立一对一关联。
     * CascadeType.ALL: 当保存/删除 Requirement 时，关联的 ArchitecturalRequirement 也会被一并保存/删除。
     * mappedBy = "requirement": 表示这个关联关系由 ArchitecturalRequirement 实体中的 "requirement" 字段来维护。
     */
    @OneToOne(mappedBy = "requirement", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private ArchitecturalRequirement architecturalRequirement;

    // 删除旧的架构字段
    // private Boolean hasArchPlan;
    // private LocalDate archPlanDeliveryDate;
    // ... 删除所有旧的架构字段 ...
    // === END: 关键修改 ===
}
