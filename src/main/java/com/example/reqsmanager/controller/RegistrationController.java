package com.example.reqsmanager.controller;

import com.example.reqsmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    /**
     * 显示注册页面.
     */
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    /**
     * 处理注册表单提交.
     */
    @PostMapping("/register")
    public String processRegistration(@RequestParam String username,
                                      @RequestParam String password,
                                      @RequestParam String verificationCode,
                                      RedirectAttributes redirectAttributes) {
        try {
            userService.registerNewUser(username, password, verificationCode);
            // 注册成功，重定向到登录页，并附带成功消息
            redirectAttributes.addFlashAttribute("registrationSuccess", "注册成功！请登录。");
            return "redirect:/login";
        } catch (Exception e) {
            // 注册失败，重定向回注册页，并附带错误消息
            redirectAttributes.addFlashAttribute("registrationError", e.getMessage());
            return "redirect:/register";
        }
    }
}
