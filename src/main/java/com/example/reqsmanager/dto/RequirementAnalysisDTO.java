package com.example.reqsmanager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RequirementAnalysisDTO {
    private Integer id;
    private String reqId;
    private String name;
    private Boolean isAnalysisInvolved;
    private String analysisMembers;
    private LocalDate analysisFinishDate;
    private String analysisOutput;
    private Boolean hasSpec;
    private String specWriter;
    private Boolean isSpecReviewed;
    private LocalDate specReviewTime;
}
