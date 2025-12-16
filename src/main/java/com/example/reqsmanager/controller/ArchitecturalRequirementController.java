package com.example.reqsmanager.controller;

import com.example.reqsmanager.dto.ArchitecturalRequirementDTO;
import com.example.reqsmanager.dto.ReviewInfoDTO;
import com.example.reqsmanager.entity.ArchitecturalRequirement;
import com.example.reqsmanager.entity.Requirement;
import com.example.reqsmanager.entity.ReviewInfo;
import com.example.reqsmanager.service.ArchitecturalRequirementService;
import com.example.reqsmanager.service.RequirementService;
import com.example.reqsmanager.service.ReviewInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/architectural")
public class ArchitecturalRequirementController {

    @Autowired
    private RequirementService requirementService;

    @Autowired
    private ReviewInfoService reviewInfoService;

    @Autowired
    private ArchitecturalRequirementService architecturalRequirementService;

    @GetMapping("/")
    public String list(Model model, @RequestParam(required = false) String reqId, Pageable pageable) {
        Page<Requirement> page = requirementService.findRequirements(reqId, pageable);
        model.addAttribute("page", page);
        model.addAttribute("reqId", reqId);
        model.addAttribute("view", "architectural/list");
        return "layout";
    }

    // === START: 只保留这一个处理 /edit/{id} 的方法 ===
    /**
     * 显示“详情/编辑”的统一表单页。
     * 这个方法现在是访问架构需求详细信息的唯一入口。
     */
    @GetMapping("/edit/{id}")
    public String showDetailsAndEditForm(@PathVariable Integer id, Model model) {
        Requirement req = requirementService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid requirement Id:" + id));

        ArchitecturalRequirement archReq = req.getArchitecturalRequirement();

        if (archReq == null) {
            archReq = new ArchitecturalRequirement();
            archReq.setRequirement(req);
        }

        if (archReq.getReviewInfo() == null) {
            ReviewInfo newReviewInfo = new ReviewInfo();
            archReq.setReviewInfo(newReviewInfo);
            // 保存以确保 ReviewInfo 记录被创建并关联
            architecturalRequirementService.save(archReq);
        }

        // --- 将所有数据映射到一个 DTO ---
        ArchitecturalRequirementDTO dto = new ArchitecturalRequirementDTO();

        dto.setRequirementId(req.getId());
        dto.setReqId(req.getReqId());
        dto.setName(req.getName());

        dto.setId(archReq.getId());
        dto.setImportantRequirement(archReq.getImportantRequirement());
        dto.setSummaryDesignSubmitted(archReq.getSummaryDesignSubmitted());
        dto.setSummaryDesignSubmitter(archReq.getSummaryDesignSubmitter());
        dto.setSummaryDesignSubmitDate(archReq.getSummaryDesignSubmitDate());
        dto.setSummaryDesignReviewPassDate(archReq.getSummaryDesignReviewPassDate());
        dto.setDetailedDesignSubmitted(archReq.getDetailedDesignSubmitted());
        dto.setInvolvesArchDecision(archReq.getInvolvesArchDecision());
        dto.setInvolvesInfra(archReq.getInvolvesInfra());
        dto.setInvolvesSeniorReport(archReq.getInvolvesSeniorReport());
        dto.setDetailedDesignSubmitter(archReq.getDetailedDesignSubmitter());
        dto.setDetailedDesignSubmitDate(archReq.getDetailedDesignSubmitDate());

        // === START: 补全这四个字段的映射 ===
        dto.setSummaryDesignScore(archReq.getSummaryDesignScore());
        dto.setSummaryDesignDeductionReason(archReq.getSummaryDesignDeductionReason());
        dto.setDetailedDesignScore(archReq.getDetailedDesignScore());
        dto.setDetailedDesignDeductionReason(archReq.getDetailedDesignDeductionReason());
        // === END ===

        ReviewInfo reviewInfo = archReq.getReviewInfo();
        dto.setReviewInfoId(reviewInfo.getId());
        dto.setReviewCheck1(reviewInfo.getReviewCheck1());
        dto.setReviewCheck2(reviewInfo.getReviewCheck2());
        dto.setReviewCheck3(reviewInfo.getReviewCheck3());
        dto.setReviewCheck4(reviewInfo.getReviewCheck4());
        dto.setReviewCheck5(reviewInfo.getReviewCheck5());
        dto.setReviewCheck6(reviewInfo.getReviewCheck6());
        dto.setReviewCheck7(reviewInfo.getReviewCheck7());
        dto.setReviewCheck8(reviewInfo.getReviewCheck8());
        dto.setReviewCheck9(reviewInfo.getReviewCheck9());
        dto.setReviewLevel(reviewInfo.getReviewLevel());
        dto.setAuditCheck1(reviewInfo.getAuditCheck1());
        dto.setAuditCheck2(reviewInfo.getAuditCheck2());
        dto.setAuditCheck3(reviewInfo.getAuditCheck3());
        dto.setAuditCheck4(reviewInfo.getAuditCheck4());
        dto.setAuditCheck5(reviewInfo.getAuditCheck5());
        dto.setAuditCheck6(reviewInfo.getAuditCheck6());
        dto.setAuditCheck7(reviewInfo.getAuditCheck7());
        dto.setAuditCheck8(reviewInfo.getAuditCheck8());
        dto.setAuditCheck9(reviewInfo.getAuditCheck9());
        dto.setAuditLevel(reviewInfo.getAuditLevel());

        model.addAttribute("dto", dto);
        model.addAttribute("pageTitle", "架构需求详情与维护");

        return "architectural/form";
    }
    // === END: 只保留这一个 ===

    @PostMapping("/save")
    public String save(@ModelAttribute("dto") ArchitecturalRequirementDTO dto) {
        architecturalRequirementService.saveFromDto(dto);
        return "redirect:/architectural/";
    }

}
