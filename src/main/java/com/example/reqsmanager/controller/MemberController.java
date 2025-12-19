package com.example.reqsmanager.controller;

import com.example.reqsmanager.entity.Member;
import com.example.reqsmanager.service.MemberService;
import com.example.reqsmanager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members")
public class MemberController {

    @Autowired private MemberService memberService;
    @Autowired private TeamService teamService;

    @GetMapping("/")
    public String list(Model model) {
        model.addAttribute("members", memberService.findAllMembers());
        model.addAttribute("view", "members/list");
        return "layout";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("member", new Member());
        // 调用新的 Service 方法
        model.addAttribute("teams", teamService.findAllTeamsForForm());
        model.addAttribute("pageTitle", "新增成员");
        return "members/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("member", memberService.findById(id));
        model.addAttribute("teams", teamService.findAllTeamsForForm());
        model.addAttribute("pageTitle", "修改成员信息");
        return "members/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Member member,
                       @RequestParam Integer teamId,
                       @RequestParam Integer groupId) {
        memberService.saveMember(member, teamId, groupId);
        return "redirect:/members/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        memberService.deleteById(id);
        return "redirect:/members/";
    }
}
