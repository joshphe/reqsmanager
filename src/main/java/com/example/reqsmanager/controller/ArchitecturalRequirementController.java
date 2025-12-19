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
import org.springframework.data.domain.PageRequest;
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

    // === START: 核心修正此方法 ===
    /**
     * 显示列表页，并处理分页和筛选。
     */
    @GetMapping("/")
    public String list(Model model,
                       @RequestParam(required = false) String reqId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {

        // 1. 根据传入的 page 和 size 参数，手动创建 Pageable 对象
        Pageable pageable = PageRequest.of(page, size);

        // 2. 将 pageable 对象传递给 Service 层
        Page<Requirement> requirementPage = requirementService.findRequirements(reqId, pageable);

        // 3. 将返回的 Page 对象和 size 一并传给前端
        model.addAttribute("page", requirementPage);
        model.addAttribute("reqId", reqId);
        model.addAttribute("size", size); // 确保 size 被传递，以便翻页链接能保持每页条数

        model.addAttribute("view", "architectural/list");
        return "layout";
    }
    // === END: 核心修正 ===

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
            architecturalRequirementService.save(archReq);
        }

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
        dto.setInvolvesArchDecision(archReq.getInvolvesArchDecision());
        dto.setInvolvesInfra(archReq.getInvolvesInfra());
        dto.setInvolvesSeniorReport(archReq.getInvolvesSeniorReport());
        dto.setSummaryDesignScore(archReq.getSummaryDesignScore());
        dto.setSummaryDesignDeductionReason(archReq.getSummaryDesignDeductionReason());

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

    @PostMapping("/save")
    public String save(@ModelAttribute("dto") ArchitecturalRequirementDTO dto) {
        architecturalRequirementService.saveFromDto(dto);
        return "redirect:/architectural/";
    }

}
