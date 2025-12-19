package com.example.reqsmanager.repository;

import com.example.reqsmanager.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    // 根据姓名查找成员
    Optional<Member> findByName(String name);
}
