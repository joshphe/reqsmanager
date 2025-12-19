package com.example.reqsmanager.service;

import com.example.reqsmanager.dto.GroupForFormDTO;
import com.example.reqsmanager.dto.TeamForFormDTO;
import com.example.reqsmanager.entity.Team;
import com.example.reqsmanager.entity.TeamGroup;
import com.example.reqsmanager.repository.TeamGroupRepository;
import com.example.reqsmanager.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {
    @Autowired private TeamRepository teamRepository;
    @Autowired private TeamGroupRepository teamGroupRepository;

    public List<Team> findAllTeams() {
        return teamRepository.findAll();
    }
    public Team findTeamById(Integer id) {
        return teamRepository.findById(id).orElseThrow(() -> new RuntimeException("Team not found"));
    }
    public void saveTeam(Team team) {
        teamRepository.save(team);
    }
    public void deleteTeamById(Integer id) {
        teamRepository.deleteById(id);
    }

    @Transactional
    public void addGroupToTeam(Integer teamId, String groupName) {
        Team team = findTeamById(teamId);
        TeamGroup newGroup = new TeamGroup();
        newGroup.setName(groupName);
        newGroup.setTeam(team);
        team.getGroups().add(newGroup); // 维护关联关系
        teamRepository.save(team); // 因为 CascadeType.ALL, 会自动保存 newGroup
    }

    public void deleteGroupById(Integer groupId) {
        teamGroupRepository.deleteById(groupId);
    }

    public List<TeamForFormDTO> findAllTeamsForForm() {
        return teamRepository.findAll().stream()
                .map(team -> {
                    TeamForFormDTO teamDTO = new TeamForFormDTO();
                    teamDTO.setId(team.getId());
                    teamDTO.setName(team.getName());

                    List<GroupForFormDTO> groupDTOs = team.getGroups().stream()
                            .map(group -> {
                                GroupForFormDTO groupDTO = new GroupForFormDTO();
                                groupDTO.setId(group.getId());
                                groupDTO.setName(group.getName());
                                return groupDTO;
                            }).collect(Collectors.toList());

                    teamDTO.setGroups(groupDTOs);
                    return teamDTO;
                }).collect(Collectors.toList());
    }
}
