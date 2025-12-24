package com.example.reqsmanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "architecture_decisions")
public class ArchitectureDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String decisionNumber;

    @Column(length = 2000)
    private String decisionContent;

    @Column(length = 50)
    private String proposer;

    @Column(length = 100)
    private String teamName;

    private LocalDate proposalDate;

    @Column(length = 255)
    private String decisionMaker;

    private LocalDate decisionDate;

    @Column(length = 50)
    private String reqId;

    @Column(columnDefinition = "TEXT")
    private String affectedApplications;

    @Column(length = 100)
    private String architectureDomain;

    @Column(columnDefinition = "TEXT")
    private String problemDescription;

    @Column(columnDefinition = "TEXT")
    private String assumptionsAndConstraints;

    @Column(columnDefinition = "TEXT")
    private String alternativeSolutions;

    @Column(columnDefinition = "TEXT")
    private String decisionRationale;

    @Column(columnDefinition = "TEXT")
    private String natureOfControversy;

    @Column(columnDefinition = "TEXT")
    private String impactAnalysis;

    @Column(columnDefinition = "TEXT")
    private String derivedRequirements;

    @Column(columnDefinition = "TEXT")
    private String relatedArchDecisions;
}
