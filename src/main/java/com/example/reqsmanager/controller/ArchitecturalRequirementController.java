package com.example.reqsmanager.controller;

import com.example.reqsmanager.dto.ArchitecturalRequirementDTO;
import com.example.reqsmanager.entity.Requirement;
import com.example.reqsmanager.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
// 确保 @RequestMapping 是 "/architectural"
@RequestMapping("/architectural")
public class ArchitecturalRequirementController {

    @Autowired
    private RequirementService requirementService;

    /**
     * 显示列表页.
     * 映射到 GET /architectural/
     */
    @GetMapping("/")
    public String list(Model model,
                       @RequestParam(required = false) String reqId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Requirement> requirementPage = requirementService.findRequirements(reqId, pageable);

        model.addAttribute("page", requirementPage);
        model.addAttribute("reqId", reqId);

        // 确保 view 的值与 templates 目录下的路径一致
        model.addAttribute("view", "architectural/list");
        return "layout";
    }

    // ... 在 ArchitecturalRequirementController.java 的 showEditForm 方法中 ...
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Requirement req = requirementService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid requirement Id:" + id));

        if (req.getArchitecturalRequirement() == null) {
            req.setArchitecturalRequirement(new com.example.reqsmanager.entity.ArchitecturalRequirement());
        }

        ArchitecturalRequirementDTO dto = new ArchitecturalRequirementDTO();
        dto.setId(req.getArchitecturalRequirement().getId());
        dto.setRequirementId(req.getId());
        dto.setReqId(req.getReqId());
        dto.setName(req.getName());

        // === START: 使用新的 DTO 字段名进行设置 ===
        dto.setImportantRequirement(req.getArchitecturalRequirement().getIsImportantRequirement());
        dto.setSummaryDesignSubmitted(req.getArchitecturalRequirement().getIsSummaryDesignSubmitted());
        dto.setDetailedDesignSubmitted(req.getArchitecturalRequirement().getIsDetailedDesignSubmitted());
        dto.setInvolvesArchDecision(req.getArchitecturalRequirement().getInvolvesArchDecision());
        dto.setInvolvesInfra(req.getArchitecturalRequirement().getInvolvesInfra());
        dto.setInvolvesSeniorReport(req.getArchitecturalRequirement().getInvolvesSeniorReport());
        // === END: 使用新的 DTO 字段名 ===

        // ... 其他非布尔字段的映射保持不变 ...
        dto.setSummaryDesignSubmitter(req.getArchitecturalRequirement().getSummaryDesignSubmitter());
        dto.setSummaryDesignSubmitDate(req.getArchitecturalRequirement().getSummaryDesignSubmitDate());
        // ... etc.

        model.addAttribute("dto", dto);
        return "architectural/form";
    }


    /**
     * 保存表单数据.
     * 映射到 POST /architectural/save
     */
    @PostMapping("/save")
    public String save(@ModelAttribute("dto") ArchitecturalRequirementDTO dto) {
        requirementService.saveArchitecturalInfo(dto);
        return "redirect:/architectural/";
    }
}
