package com.example.reqsmanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RequirementArchitectureDTO {
    private Integer id;
    private String reqId;
    private String name;
    private Boolean hasArchPlan;
    private LocalDate archPlanDeliveryDate;
    private LocalDate archPlanReviewTime;
    private LocalDate designDeliveryDate;
    private LocalDate designReviewTime;
    private LocalDate codeReviewTime;
}
