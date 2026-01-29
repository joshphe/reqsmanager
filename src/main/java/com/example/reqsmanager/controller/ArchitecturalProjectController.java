package com.example.reqsmanager.controller;

import com.example.reqsmanager.entity.ArchitecturalProject;
import com.example.reqsmanager.service.ArchitecturalProjectService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // 确保 Pageable 已导入
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/arch-projects")
public class ArchitecturalProjectController {

    @Autowired
    private ArchitecturalProjectService projectService;

    /**
     * 显示项目列表页.
     */
    @GetMapping("/")
    public String list(Model model,
                       // === START: 新增筛选参数 ===
                       @RequestParam(required = false) String projectNumber,
                       @RequestParam(required = false) String reqId, // 保持旧有的
                       @RequestParam(required = false) String reqName,
                       @RequestParam(required = false) String projectManager,
                       @RequestParam(required = false) Boolean isKeyProject,
                       // === END ===
                       Pageable pageable) { // 直接使用 Pageable, Spring 会自动创建
        // === START: 将所有参数传递给 Service ===
        Page<ArchitecturalProject> architecturalProjectPage = projectService.findProjects(
                projectNumber, reqId, reqName, projectManager, isKeyProject, pageable);
        // === END ===
        model.addAttribute("page", architecturalProjectPage);
        model.addAttribute("reqId", reqId);
        // === START: 将所有参数回传给前端 ===
        model.addAttribute("projectNumber", projectNumber);
        model.addAttribute("reqId", reqId);
        model.addAttribute("reqName", reqName);
        model.addAttribute("projectManager", projectManager);
        model.addAttribute("isKeyProject", isKeyProject);
        // === END ===
        model.addAttribute("view", "arch-projects/list");
        return "layout";
    }

    // === START: 新增此方法以支持“新增项目”功能 ===
    /**
     * 显示“新增项目”的空白表单页.
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        // 1. 创建一个空的项目对象，并绑定到 model
        model.addAttribute("project", new ArchitecturalProject());
        // 2. 设置页面标题
        model.addAttribute("pageTitle", "新增架构项目");
        // 3. 返回表单模板，Thymeleaf 会渲染一个空表单
        return "arch-projects/form";
    }
    // === END: 新增此方法 ===

    /**
     * 显示“维护项目”的表单页，并填充现有数据.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("project", projectService.findById(id));
        model.addAttribute("pageTitle", "维护架构项目");
        return "arch-projects/form";
    }

    /**
     * 保存新增或维护后的项目信息.
     */
    @PostMapping("/save")
    public String save(@ModelAttribute("project") ArchitecturalProject project) {
        projectService.save(project);
        return "redirect:/arch-projects/";
    }

    /**
     * 删除项目.
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        projectService.deleteById(id);
        return "redirect:/arch-projects/";
    }

    // === START: 新增的导出方法 ===
    /**
     * 导出所有架构项目数据为 CSV 文件.
     * @param response HttpServletResponse 对象，用于直接写入文件流
     */
    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        // 1. 设置 HTTP 响应头
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=\"architectural_projects_export.csv\"");

        // 2. 获取所有数据
        List<ArchitecturalProject> projects = projectService.findAll();

        // 3. 定义 CSV 表头 (与 list.html 页面保持一致)
        String[] headers = {
                "项目编号", "需求编号", "需求名称", "是否重点项目", "可行性方案递交人", "可行性方案递交日期",
                "可行性方案评审通过日期", "总体设计递交人", "总体设计递交日期", "总体设计评审通过日期",
                "详细设计递交人", "详细设计递交日期", "可行性方案评分", "可行性方案扣分原因", "总体设计评分",
                "总体设计扣分原因", "详细设计评分", "详细设计扣分原因", "备注"
        };

        try (PrintWriter writer = response.getWriter()) {
            // 写入 BOM 以兼容 Excel
            writer.write('\ufeff');
            // 写入表头行
            writer.println(String.join(",", headers));

            // 4. 遍历数据并写入每一行
            for (ArchitecturalProject proj : projects) {
                String[] data = {
                        escapeCsv(proj.getProjectNumber()),
                        escapeCsv(proj.getReqId()),
                        escapeCsv(proj.getReqName()),
                        escapeCsv(proj.getKeyProject()),
                        escapeCsv(proj.getFeasibilitySubmitter()),
                        escapeCsv(proj.getFeasibilitySubmitDate()),
                        escapeCsv(proj.getFeasibilityReviewPassDate()),
                        escapeCsv(proj.getGeneralDesignSubmitter()),
                        escapeCsv(proj.getGeneralDesignSubmitDate()),
                        escapeCsv(proj.getGeneralDesignReviewPassDate()),
                        escapeCsv(proj.getDetailedDesignSubmitter()),
                        escapeCsv(proj.getDetailedDesignSubmitDate()),
                        escapeCsv(proj.getFeasibilityScore()),
                        escapeCsv(proj.getFeasibilityDeductionReason()),
                        escapeCsv(proj.getGeneralDesignScore()),
                        escapeCsv(proj.getGeneralDesignDeductionReason()),
                        escapeCsv(proj.getDetailedDesignScore()),
                        escapeCsv(proj.getDetailedDesignDeductionReason()),
                        escapeCsv(proj.getRemarks())
                };
                writer.println(String.join(",", data));
            }
        }
    }

    /**
     * 处理 CSV 中的特殊字符.
     */
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
    // === END: 新增的导出方法 ===

    // === START: 新增批量导入的处理方法 ===
    /**
     * 处理 CSV 文件的上传和导入请求。
     */
    @PostMapping("/import")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "请选择一个 CSV 文件上传！");
            return "redirect:/arch-projects/";
        }

        try {
            String message = projectService.importFromCsv(file);
            redirectAttributes.addFlashAttribute("success", message);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "文件导入失败: " + e.getMessage());
        }

        return "redirect:/arch-projects/";
    }
    // === END ===

    // === START: 新增导入模板下载方法 ===
    /**
     * 提供 CSV 导入模板的下载功能。
     */
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=\"arch_projects_import_template.csv\"");

        String[] headers = {
                "项目编号", "需求编号", "需求名称", "开发部室", "项目负责人"
        };

        String[] exampleData = {
                "ARCH-PROJ-DEMO-001",
                "REQ-001",
                "核心系统升级",
                "创新开发部",
                "王小明"
        };

        try (PrintWriter writer = response.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) { // 使用 CSVPrinter 写入

            writer.write('\ufeff'); // BOM for Excel
            csvPrinter.printRecord((Object[]) headers); // 写入表头
            csvPrinter.printRecord((Object[]) exampleData); // 写入样例数据
        }
    }
    // === END ===


}
