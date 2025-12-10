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
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/requirements")
public class RequirementController {

    @Autowired
    private RequirementService requirementService;

    // === START: 注入 EntityManager 用于原生 SQL 查询 ===
    @Autowired
    private EntityManager entityManager;
    // === END: 注入 ===

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

    // === START: 新增的导出方法 ===
    /**
     * 导出 reqsmanager 全表数据为 CSV 文件.
     * @param response HttpServletResponse 对象，用于直接写入文件流
     */
    @GetMapping("/export")
    @Transactional(readOnly = true) // 使用事务，并设置为只读以优化性能
    public void exportToCsv(HttpServletResponse response) throws IOException {
        // 1. 设置 HTTP 响应头
        response.setContentType("text/csv; charset=UTF-8");
        // 添加 BOM (Byte Order Mark) 以确保 Excel 能正确识别 UTF-8 编码
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=\"requirements_export.csv\"");

        // 2. 获取所有数据
        List<Requirement> requirements = requirementService.findAll(); // 假设 Service 中有这个方法

        // 3. 获取 CSV 表头 (从数据库 Comment)
        Map<String, String> headers = getColumnComments();
        String headerRow = String.join(",", headers.values());

        // 4. 写入 CSV 内容
        try (PrintWriter writer = response.getWriter()) {
            // 写入 BOM
            writer.write('\ufeff');
            // 写入表头
            writer.write(headerRow);
            writer.write("\n");

            // 5. 遍历数据并写入每一行
            for (Requirement req : requirements) {
                // 注意：这里的顺序必须和 getColumnComments() 中查询的顺序严格一致！
                String[] data = {
                        escapeCsv(req.getReqId()),
                        escapeCsv(req.getName()),
                        escapeCsv(req.getBusinessLeader()),
                        escapeCsv(req.getTechLeader()),
                        escapeCsv(req.getBusinessLine()),
                        escapeCsv(req.getDevLeader()),
                        escapeCsv(req.getScheduleDate()),
                        escapeCsv(req.getIsAnalysisInvolved()),
                        escapeCsv(req.getAnalysisMembers()),
                        escapeCsv(req.getAnalysisFinishDate()),
                        escapeCsv(req.getAnalysisOutput()),
                        escapeCsv(req.getHasSpec()),
                        escapeCsv(req.getSpecWriter()),
                        escapeCsv(req.getIsSpecReviewed()),
                        escapeCsv(req.getSpecReviewTime()),
                        escapeCsv(req.getHasArchPlan()),
                        escapeCsv(req.getArchPlanDeliveryDate()),
                        escapeCsv(req.getArchPlanReviewTime()),
                        escapeCsv(req.getDesignDeliveryDate()),
                        escapeCsv(req.getDesignReviewTime()),
                        escapeCsv(req.getCodeReviewTime())
                };
                writer.write(String.join(",", data));
                writer.write("\n");
            }
        }
    }

    /**
     * 从 information_schema 获取表字段的注释作为表头.
     * @return 一个有序的 Map，Key 是字段名，Value 是注释
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> getColumnComments() {
        String dbName = "reqsmanager"; // 替换成您的数据库名
        String tableName = "requirements";

        String sql = "SELECT COLUMN_NAME, COLUMN_COMMENT " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = :dbName AND TABLE_NAME = :tableName " +
                "AND COLUMN_NAME != 'id' " + // 排除 id 字段
                "ORDER BY ORDINAL_POSITION";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dbName", dbName);
        query.setParameter("tableName", tableName);

        List<Object[]> results = query.getResultList();

        // 使用 Collectors.toMap 保持插入顺序
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (String) row[1],
                        (oldValue, newValue) -> oldValue,
                        java.util.LinkedHashMap::new
                ));
    }

    /**
     * 处理 CSV 中的特殊字符，例如包含逗号或引号的字段.
     * @param value 原始值
     * @return 处理后的 CSV 字段值
     */
    private String escapeCsv(Object value) {
        if (value == null) {
            return "";
        }
        String stringValue = value.toString();
        if (stringValue.contains(",") || stringValue.contains("\"") || stringValue.contains("\n")) {
            // 如果字段包含逗号、引号或换行符，用双引号括起来，并将内部的双引号替换为两个双引号
            return "\"" + stringValue.replace("\"", "\"\"") + "\"";
        }

        // Boolean 类型转为 "是"/"否"
        if (value instanceof Boolean) {
            return (Boolean) value ? "是" : "否";
        }

        return stringValue;
    }
    // === END: 新增的导出相关方法 ===
}
