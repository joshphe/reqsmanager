package com.example.reqsmanager.service;

import com.example.reqsmanager.entity.Member;
import com.example.reqsmanager.entity.Team;
import com.example.reqsmanager.entity.TeamGroup;
import com.example.reqsmanager.repository.MemberRepository;
import com.example.reqsmanager.repository.TeamGroupRepository;
import com.example.reqsmanager.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    @Autowired private MemberRepository memberRepository;
    @Autowired private TeamRepository teamRepository;
    @Autowired private TeamGroupRepository teamGroupRepository;

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }
    public Member findById(Integer id) {
        return memberRepository.findById(id).orElseThrow(() -> new RuntimeException("Member not found"));
    }
    public Optional<Member> findMemberByName(String name) {
        return memberRepository.findByName(name);
    }

    public void saveMember(Member member, Integer teamId, Integer groupId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));
        TeamGroup group = teamGroupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));

        member.setTeam(team);
        member.setTeamGroup(group);

        memberRepository.save(member);
    }

    public void deleteById(Integer id) {
        memberRepository.deleteById(id);
    }
}
