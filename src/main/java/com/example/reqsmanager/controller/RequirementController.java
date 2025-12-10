package com.example.reqsmanager.controller;

import com.example.reqsmanager.dto.RequirementGeneralDTO;
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
@RequestMapping("/requirements")
public class RequirementController {

    @Autowired
    private RequirementService requirementService;

    @GetMapping("/")
    public String list(Model model,
                       @RequestParam(required = false) String reqId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Requirement> requirementPage = requirementService.findRequirements(reqId, pageable);

        model.addAttribute("page", requirementPage);
        model.addAttribute("reqId", reqId);
        model.addAttribute("size", size);

        model.addAttribute("view", "requirements/list");
        return "layout";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("dto", new RequirementGeneralDTO());
        model.addAttribute("pageTitle", "新增需求");
        return "requirements/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Requirement req = requirementService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid requirement Id:" + id));

        RequirementGeneralDTO dto = new RequirementGeneralDTO();
        dto.setId(req.getId());
        dto.setReqId(req.getReqId());
        dto.setName(req.getName());
        dto.setBusinessLeader(req.getBusinessLeader());
        dto.setTechLeader(req.getTechLeader());
        dto.setBusinessLine(req.getBusinessLine());
        dto.setDevLeader(req.getDevLeader());
        dto.setScheduleDate(req.getScheduleDate());

        model.addAttribute("dto", dto);
        model.addAttribute("pageTitle", "编辑需求");
        return "requirements/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("dto") RequirementGeneralDTO dto) {
        if (dto.getId() == null) {
            requirementService.createNewRequirement(dto);
        } else {
            requirementService.saveGeneralInfo(dto);
        }
        return "redirect:/requirements/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        requirementService.deleteById(id);
        return "redirect:/requirements/";
    }
}
