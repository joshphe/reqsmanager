package com.example.reqsmanager.controller;

import com.example.reqsmanager.entity.ArchitectureDecision;
import com.example.reqsmanager.service.ArchitectureDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/arch-decisions")
public class ArchitectureDecisionController {

    @Autowired
    private ArchitectureDecisionService decisionService;

    @GetMapping("/")
    public String list(Model model, @RequestParam(required = false) String reqId, Pageable pageable) {
        Page<ArchitectureDecision> page = decisionService.findDecisions(reqId, pageable);
        model.addAttribute("page", page);
        model.addAttribute("reqId", reqId);
        model.addAttribute("view", "arch-decisions/list");
        return "layout";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("decision", new ArchitectureDecision());
        model.addAttribute("pageTitle", "新增架构决策");
        return "arch-decisions/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("decision", decisionService.findById(id));
        model.addAttribute("pageTitle", "架构决策详情与维护");
        return "arch-decisions/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ArchitectureDecision decision) {
        decisionService.save(decision);
        return "redirect:/arch-decisions/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        decisionService.deleteById(id);
        return "redirect:/arch-decisions/";
    }
}
