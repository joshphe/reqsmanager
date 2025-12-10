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
    private String businessLine;
    private String devLeader;
    private LocalDate scheduleDate;
}
