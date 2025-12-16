package com.example.reqsmanager.dto;

import lombok.Data;

/**
 * 用于在“架构需求管理”的详情弹窗中，传输评审与检核信息的数据传输对象 (DTO)。
 * 这个类将前端表单的数据传递给 Controller。
 */
@Data
public class ReviewInfoDTO {

    /**
     * ReviewInfo 实体在数据库中的主键 ID。
     * 这个字段在表单中是隐藏的，但在提交保存时至关重要，
     * 它告诉后端服务要更新的是数据库中的哪一条记录。
     */
    private Integer id;

    // --- “评审信息”部分的字段 ---
    private Boolean reviewCheck1;
    private Boolean reviewCheck2;
    private Boolean reviewCheck3;
    private Boolean reviewCheck4;
    private Boolean reviewCheck5;
    private Boolean reviewCheck6;
    private Boolean reviewCheck7;
    private Boolean reviewCheck8;
    private Boolean reviewCheck9;

    /**
     * “评审信息”部分的评审级别。
     * 这个字段的值由前端 JavaScript 根据 reviewCheck1-9 的勾选情况动态计算，然后提交到后端。
     */
    private String reviewLevel;

    // --- “架构检核”部分的字段 ---
    private Boolean auditCheck1;
    private Boolean auditCheck2;
    private Boolean auditCheck3;
    private Boolean auditCheck4;
    private Boolean auditCheck5;
    private Boolean auditCheck6;
    private Boolean auditCheck7;
    private Boolean auditCheck8;
    private Boolean auditCheck9;

    /**
     * “架构检核”部分的评审级别。
     * 这个字段的值也由前端 JavaScript 动态计算后提交。
     */
    private String auditLevel;
}
