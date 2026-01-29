package com.example.reqsmanager.controller;

import com.example.reqsmanager.dto.RequirementExportDTO;
import com.example.reqsmanager.dto.RequirementGeneralDTO;
import com.example.reqsmanager.entity.Requirement;
import com.example.reqsmanager.service.RequirementService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/requirements")
public class RequirementController {

    @Autowired
    private RequirementService requirementService;

    /**
     * 显示“需求管理”模块的主列表页.
     */
    @GetMapping("/")
    public String list(Model model,
                       @RequestParam(required = false) String reqId,
                       // === START: 新增筛选参数 ===
                       @RequestParam(required = false) String reqName,
                       @RequestParam(required = false) String techLeader,
                       // === START: 替换 scheduleDate 为 startDate 和 endDate ===
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                       // === END ===
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        // === START: 将新参数传递给 Service ===
        Page<Requirement> requirementPage = requirementService.findRequirements(reqId, reqName, techLeader, startDate, endDate, null, null, pageable);
        // === END ===

        model.addAttribute("page", requirementPage);
        model.addAttribute("reqId", reqId);
        model.addAttribute("size", size);
        // === START: 将新参数回传给前端，以便筛选表单和分页链接保持状态 ===
        model.addAttribute("reqName", reqName);
        model.addAttribute("techLeader", techLeader);
        // === START: 回传 startDate 和 endDate ===
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        // === END ===
        model.addAttribute("view", "requirements/list");
        return "layout";
    }

    /**
     * 显示“新增需求”的表单页.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("dto", new RequirementGeneralDTO());
        model.addAttribute("pageTitle", "新增需求");
        return "requirements/form";
    }

    /**
     * 显示“编辑需求”的表单页.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Requirement req = requirementService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid requirement Id:" + id));

        // 将实体(Entity)转换为 DTO
        RequirementGeneralDTO dto = new RequirementGeneralDTO();
        dto.setId(req.getId());
        dto.setReqId(req.getReqId());
        dto.setName(req.getName());
        dto.setBusinessLeader(req.getBusinessLeader());
        dto.setTechLeader(req.getTechLeader());
        // === START: 新增字段的映射 ===
        dto.setLeadDepartment(req.getLeadDepartment());
        // === START: 新增 ===
        dto.setGroupName(req.getGroupName());
        // === END ===
        dto.setReqType(req.getReqType());
        // === END: 新增 ===
        dto.setBusinessLine(req.getBusinessLine());
        dto.setDevLeader(req.getDevLeader());
        dto.setScheduleDate(req.getScheduleDate());

        model.addAttribute("dto", dto);
        model.addAttribute("pageTitle", "编辑需求");
        return "requirements/form";
    }

    /**
     * 保存新增或编辑后的需求信息.
     */
    @PostMapping("/save")
    public String save(@ModelAttribute("dto") RequirementGeneralDTO dto) {
        if (dto.getId() == null) {
            requirementService.createNewRequirement(dto);
        } else {
            requirementService.saveGeneralInfo(dto);
        }
        return "redirect:/requirements/";
    }

    /**
     * 删除需求.
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        requirementService.deleteById(id);
        return "redirect:/requirements/";
    }

    // === START: 彻底重构导出方法 ===
    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        // 1. 设置 HTTP 响应头
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=\"requirements_full_export.csv\"");

        // 2. 获取用于导出的“扁平化”数据列表
        List<RequirementExportDTO> exportData = requirementService.findAllForExport();

        // 3. 动态生成表头 (通过反射)
        // === START: 1. 在表头数组中添加“需求类型” ===
        String[] headers = {
                "需求编号", "需求名称", "业务负责人", "科技负责人", "牵头部室", "所属小组", "需求类型", // 已添加
                "业务条线", "开发负责人", "需求排期", "是否重要需求", "是否递交概要设计",
                "概要设计递交人", "概要设计递交日期", "概要设计评审通过日期", "是否涉及架构决策", "是否涉及基础架构",
                "是否涉及高阶汇报", "概要设计评分", "概要设计扣分原因"
        };
        // === END ===

        try (PrintWriter writer = response.getWriter()) {
            // 写入 BOM 以兼容 Excel
            writer.write('\ufeff');
            // 写入表头行
            writer.println(String.join(",", headers));

            // 4. 遍历数据并写入每一行
            for (RequirementExportDTO dto : exportData) {
                String[] data = {
                        escapeCsv(dto.getReqId()),
                        escapeCsv(dto.getName()),
                        escapeCsv(dto.getBusinessLeader()),
                        escapeCsv(dto.getTechLeader()),
                        escapeCsv(dto.getLeadDepartment()), // 新增
                        escapeCsv(dto.getGroupName()),      // 新增
                        escapeCsv(dto.getReqType()), // 已添加
                        escapeCsv(dto.getBusinessLine()),
                        escapeCsv(dto.getDevLeader()),
                        escapeCsv(dto.getScheduleDate()),
                        escapeCsv(dto.getIsImportantRequirement()),
                        escapeCsv(dto.getIsSummaryDesignSubmitted()),
                        escapeCsv(dto.getSummaryDesignSubmitter()),
                        escapeCsv(dto.getSummaryDesignSubmitDate()),
                        escapeCsv(dto.getSummaryDesignReviewPassDate()),
//                        escapeCsv(dto.getIsDetailedDesignSubmitted()),
//                        escapeCsv(dto.getDetailedDesignSubmitter()),
//                        escapeCsv(dto.getDetailedDesignSubmitDate()),
                        escapeCsv(dto.getInvolvesArchDecision()),
                        escapeCsv(dto.getInvolvesInfra()),
                        escapeCsv(dto.getInvolvesSeniorReport()),
                        escapeCsv(dto.getSummaryDesignScore()),
                        escapeCsv(dto.getSummaryDesignDeductionReason()),
//                        escapeCsv(dto.getDetailedDesignScore()),
//                        escapeCsv(dto.getDetailedDesignDeductionReason())
                };
                writer.println(String.join(",", data));
            }
        }
    }

    // escapeCsv 方法保持不变
    private String escapeCsv(Object value) {
        if (value == null) {
            return "";
        }
        String stringValue = value.toString();
        if (value instanceof Boolean) {
            return (Boolean) value ? "是" : "否";
        }
        if (stringValue.contains(",") || stringValue.contains("\"") || stringValue.contains("\n")) {
            return "\"" + stringValue.replace("\"", "\"\"") + "\"";
        }
        return stringValue;
    }
    // === END: 彻底重构导出方法 ===

    // === START: 新增文件上传处理方法 ===
    /**
     * 处理 CSV 文件的上传和导入请求。
     */
    @PostMapping("/import")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "请选择一个 CSV 文件上传！");
            return "redirect:/requirements/";
        }

        try {
            // 调用 Service 层处理文件
            String message = requirementService.importFromCsv(file);
            redirectAttributes.addFlashAttribute("success", message);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "文件导入失败: " + e.getMessage());
        }

        return "redirect:/requirements/";
    }
    // === END ===

    // === START: 新增模板下载方法 ===
    /**
     * 提供 CSV 导入模板的下载功能。
     * @param response HttpServletResponse 对象，用于直接写入文件流
     */
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 1. 设置 HTTP 响应头
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=\"import_template.csv\"");

        // 2. 定义表头 (这个顺序必须与导入逻辑的读取顺序完全一致)
        //    根据之前的导入逻辑，我们只需要 A, B, D, E, F, N 这几列
        String[] headers = {
                "需求编号", // A
                "需求名称", // B
                "C-占位列", // C
                "需求科技负责人", // D
                "需求类型", // E
                "牵头部室", // F
                "G-占位列",
                "业务条线", "I-占位列", "J-占位列", "K-占位列", "L-占位列", "M-占位列",
                "计划投产日期" // N
        };

        // 3. 定义一行样例数据
        String[] exampleData = {
                "REQ-2025-DEMO-001", // A
                "这是一个需求名称的样例", // B
                "", // C
                "张三", // D
                "常规项目", // E
                "零售银行部", // F
                "",
                "零售业务", "", "", "", "", "",
                "2025/12/31" // N (注意日期格式 yyyy/M/d)
        };

        try (PrintWriter writer = response.getWriter()) {
            // 写入 BOM 以兼容 Excel
            writer.write('\ufeff');

            // 写入表头行
            writer.println(String.join(",", headers));

            // 写入样例数据行
            writer.println(String.join(",", exampleData));
        }
    }
    // === END ===

    // === START: 新增批量删除的处理方法 ===
    /**
     * 处理批量删除需求的请求。
     * @param ids 从前端 AJAX 请求体中获取的需求 ID 列表
     * @return 返回一个表示操作结果的 JSON 响应
     */
    @PostMapping("/delete-batch")
    @ResponseBody // 直接返回响应体，而不是视图
    public ResponseEntity<String> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"status\": \"error\", \"message\": \"未选择任何需求！\"}");
        }
        try {
            requirementService.deleteByIds(ids);
            return ResponseEntity.ok("{\"status\": \"success\", \"message\": \"批量删除成功！\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"status\": \"error\", \"message\": \"删除失败: " + e.getMessage() + "\"}");
        }
    }
    // === END ===

    // === START: 新增“一键更新”的处理方法 ===
    /**
     * 处理“一键更新”的请求。
     * @return 返回一个表示操作结果的 JSON 响应
     */
    @PostMapping("/batch-update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> batchUpdate() {
        Map<String, String> response = new HashMap<>();
        try {
            String message = requirementService.batchUpdateFromTechLeader();
            response.put("status", "success");
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    // === END ===
}
