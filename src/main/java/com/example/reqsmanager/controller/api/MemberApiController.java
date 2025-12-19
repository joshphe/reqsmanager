package com.example.reqsmanager.controller.api;

import com.example.reqsmanager.entity.Member;
import com.example.reqsmanager.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController // 关键：使用 @RestController，所有方法默认返回 JSON
@RequestMapping("/api/members")
public class MemberApiController {

    @Autowired
    private MemberService memberService;

    /**
     * 根据成员姓名查找其团队和小组信息。
     * @param name 成员姓名
     * @return 包含团队和小组名称的 JSON 对象，或 404
     */
    @GetMapping("/find-by-name")
    public ResponseEntity<?> findMemberDetailsByName(@RequestParam String name) {
        Optional<Member> memberOpt = memberService.findMemberByName(name);

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            Map<String, String> response = new HashMap<>();
            response.put("teamName", member.getTeam().getName());
            response.put("groupName", member.getTeamGroup().getName());
            return ResponseEntity.ok(response);
        } else {
            // 如果找不到，返回 404 Not Found 状态，前端可以据此判断
            return ResponseEntity.notFound().build();
        }
    }
}
