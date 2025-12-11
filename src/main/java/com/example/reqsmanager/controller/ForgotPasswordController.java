package com.example.reqsmanager.controller;

import com.example.reqsmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    /**
     * 显示“忘记密码”页面.
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    /**
     * 处理重置密码的表单提交.
     */
    @PostMapping("/forgot-password")
    public String processResetPassword(@RequestParam String username,
                                       @RequestParam String newPassword,
                                       @RequestParam String verificationCode,
                                       RedirectAttributes redirectAttributes) {
        try {
            userService.resetPassword(username, newPassword, verificationCode);
            // 成功后，重定向到登录页并显示成功消息
            redirectAttributes.addFlashAttribute("success", "密码重置成功！请使用新密码登录。");
            return "redirect:/login";
        } catch (Exception e) {
            // 失败后，重定向回忘记密码页并显示错误消息
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgot-password";
        }
    }
}
