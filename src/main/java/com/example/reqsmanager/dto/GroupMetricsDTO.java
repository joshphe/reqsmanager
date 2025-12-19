package com.example.reqsmanager.dto;

import lombok.Data;

@Data
public class GroupMetricsDTO {

    private String groupName;
    private long totalRequirements;
    private long summaryDesignsSubmitted;
    private Double averageSummaryScore; // 使用 Double 类型以接收 AVG 的结果

    /**
     * 这个构造函数非常重要，它的参数顺序和类型
     * 必须与 Repository 中 JPQL 查询的 SELECT NEW ... 部分完全匹配。
     */
    public GroupMetricsDTO(String groupName,
                           long totalRequirements,
                           long summaryDesignsSubmitted,
                           Double averageSummaryScore) {
        this.groupName = groupName;
        this.totalRequirements = totalRequirements;
        this.summaryDesignsSubmitted = summaryDesignsSubmitted;
        this.averageSummaryScore = averageSummaryScore;
    }
}
