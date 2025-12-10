package com.example.reqsmanager.controller;

import com.example.reqsmanager.dto.RequirementAnalysisDTO;
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
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    private RequirementService requirementService;

    /**
     * 显示需求分析列表页。
     *
     * @param model   用于向视图传递数据
     * @param reqId   筛选条件：需求编号
     * @param page    当前页码
     * @param size    每页显示条数
     * @return 布局模板名
     */
    @GetMapping("/")
    public String list(Model model,
                       @RequestParam(required = false) String reqId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        // 复用通用的查询方法
        Page<Requirement> requirementPage = requirementService.findRequirements(reqId, pageable);

        model.addAttribute("page", requirementPage); // 向前端传递 Page 对象
        model.addAttribute("reqId", reqId); // 回传筛选条件

        // 关键：指定内容视图，并返回布局
        model.addAttribute("view", "analysis/list");
        return "layout";
    }

    /**
     * 显示编辑表单页。
     *
     * @param id    要编辑的需求的ID
     * @param model 用于向视图传递数据
     * @return 独立的表单页面模板名
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Requirement req = requirementService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid requirement Id:" + id));

        // 将实体(Entity)转换为数据传输对象(DTO)，只暴露本页面需要的字段
        RequirementAnalysisDTO dto = new RequirementAnalysisDTO();
        dto.setId(req.getId());
        dto.setReqId(req.getReqId());
        dto.setName(req.getName());
        dto.setIsAnalysisInvolved(req.getIsAnalysisInvolved());
        dto.setAnalysisMembers(req.getAnalysisMembers());
        dto.setAnalysisFinishDate(req.getAnalysisFinishDate());
        dto.setAnalysisOutput(req.getAnalysisOutput());
        dto.setHasSpec(req.getHasSpec());
        dto.setSpecWriter(req.getSpecWriter());
        dto.setIsSpecReviewed(req.getIsSpecReviewed());
        dto.setSpecReviewTime(req.getSpecReviewTime());

        model.addAttribute("dto", dto);

        // 直接返回独立的表单页面，不使用布局
        return "analysis/form";
    }

    /**
     * 保存表单提交的数据。
     *
     * @param dto Spring自动从表单封装的数据传输对象
     * @return 重定向到列表页的URL
     */
    @PostMapping("/save")
    public String save(@ModelAttribute("dto") RequirementAnalysisDTO dto) {
        requirementService.saveAnalysisInfo(dto);
        // 保存后重定向到列表页，避免表单重复提交
        return "redirect:/analysis/";
    }
}
