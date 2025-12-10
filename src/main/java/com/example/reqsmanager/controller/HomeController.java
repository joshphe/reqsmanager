package com.example.reqsmanager.controller;

import com.example.reqsmanager.service.RequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private RequirementService requirementService;

    @GetMapping("/")
    public String home(Model model) throws Exception {

        // 1. 只获取需求总数
        long totalRequirements = requirementService.getTotalRequirements();
        model.addAttribute("totalRequirements", totalRequirements);

        // 3. 指定视图并返回布局
        model.addAttribute("view", "home");
        return "layout";
    }
}
