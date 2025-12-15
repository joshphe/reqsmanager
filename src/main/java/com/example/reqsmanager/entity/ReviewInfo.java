package com.example.reqsmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "review_infos")
public class ReviewInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 与 ArchitecturalRequirement 建立一对一的反向关联
    @OneToOne(mappedBy = "reviewInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ArchitecturalRequirement architecturalRequirement;

    // --- 评审信息字段 ---
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean reviewCheck1 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean reviewCheck2 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean reviewCheck3 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean reviewCheck4 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean reviewCheck5 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean reviewCheck6 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean reviewCheck7 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean reviewCheck8 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean reviewCheck9 = false;
    @Column(length = 50) private String reviewLevel;

    // --- 架构检核字段 ---
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean auditCheck1 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean auditCheck2 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean auditCheck3 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean auditCheck4 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean auditCheck5 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean auditCheck6 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean auditCheck7 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean auditCheck8 = false;
    @Column(columnDefinition = "BIT(1) DEFAULT 0") private Boolean auditCheck9 = false;
    @Column(length = 50) private String auditLevel;
}
