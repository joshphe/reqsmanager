package com.example.reqsmanager.controller;

import com.example.reqsmanager.entity.ArchitectureDecision;
import com.example.reqsmanager.service.ArchitectureDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/arch-decisions")
public class ArchitectureDecisionController {

    @Autowired
    private ArchitectureDecisionService decisionService;


    // === START: 新增 InitBinder，用于处理日期格式 ===
    /**
     * 初始化数据绑定。
     * 这个方法会在所有请求处理之前执行，用于注册一个自定义的日期编辑器。
     * 它告诉 Spring MVC 如何将 "yyyy-MM-dd" 格式的字符串，转换为后端需要的 Date/LocalDate 对象。
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        // true 表示允许为空
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        // 对于 LocalDate, Spring Boot 3 通常能自动处理，但显式注册更保险
        // 如果您只用 LocalDate，可以只保留针对它的注册
    }
    // === END ===
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
