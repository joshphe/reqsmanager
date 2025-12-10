package com.example.reqsmanager.repository;

import com.example.reqsmanager.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Integer>, JpaSpecificationExecutor<Requirement> {
}
