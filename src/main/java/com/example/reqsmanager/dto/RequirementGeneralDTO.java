package com.example.reqsmanager.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RequirementGeneralDTO {
    private Integer id;
    private String reqId;
    private String name;
    private String businessLeader;
    private String techLeader;
    // === START: 新增字段 ===
    private String leadDepartment;
    // === START: 新增字段 ===
    private String groupName;
    // === END: 新增字段 ===
    private String reqType;
    // === END: 新增字段 ===
    private String businessLine;
    private String devLeader;
    private LocalDate scheduleDate;
    // === START: 新增字段 ===
    private String status;
    // === END: 新增字段 ===
}
