package com.example.reqsmanager.controller;

import com.example.reqsmanager.dto.RequirementArchitectureDTO;
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
@RequestMapping("/architecture")
public class ArchitectureController {

    @Autowired
    private RequirementService requirementService;

    /**
     * 显示架构管理列表页。
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

        model.addAttribute("view", "architecture/list");
        return "layout";
    }

    /**
     * 显示编辑表单页。
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Requirement req = requirementService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid requirement Id:" + id));

        // 将实体(Entity)转换为数据传输对象(DTO)
        RequirementArchitectureDTO dto = new RequirementArchitectureDTO();
        dto.setId(req.getId());
        dto.setReqId(req.getReqId());
        dto.setName(req.getName());
        dto.setHasArchPlan(req.getHasArchPlan());
        dto.setArchPlanDeliveryDate(req.getArchPlanDeliveryDate());
        dto.setArchPlanReviewTime(req.getArchPlanReviewTime());
        dto.setDesignDeliveryDate(req.getDesignDeliveryDate());
        dto.setDesignReviewTime(req.getDesignReviewTime());
        dto.setCodeReviewTime(req.getCodeReviewTime());

        model.addAttribute("dto", dto);

        // 返回独立的表单页面
        return "architecture/form";
    }

    /**
     * 保存表单提交的数据。
     */
    @PostMapping("/save")
    public String save(@ModelAttribute("dto") RequirementArchitectureDTO dto) {
        requirementService.saveArchitectureInfo(dto);
        // 保存后重定向到列表页
        return "redirect:/architecture/";
    }
}
