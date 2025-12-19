package com.example.reqsmanager.controller;

import com.example.reqsmanager.entity.Team;
import com.example.reqsmanager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("/")
    public String list(Model model) {
        model.addAttribute("teams", teamService.findAllTeams());
        model.addAttribute("view", "teams/list");
        return "layout";
    }

    @PostMapping("/add")
    public String addTeam(@RequestParam String teamName) {
        Team newTeam = new Team();
        newTeam.setName(teamName);
        teamService.saveTeam(newTeam);
        return "redirect:/teams/";
    }

    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Integer id) {
        teamService.deleteTeamById(id);
        return "redirect:/teams/";
    }

    @PostMapping("/groups/add")
    public String addGroup(@RequestParam Integer teamId, @RequestParam String groupName) {
        teamService.addGroupToTeam(teamId, groupName);
        return "redirect:/teams/";
    }

    @GetMapping("/groups/delete/{groupId}")
    public String deleteGroup(@PathVariable Integer groupId) {
        teamService.deleteGroupById(groupId);
        return "redirect:/teams/";
    }
}
