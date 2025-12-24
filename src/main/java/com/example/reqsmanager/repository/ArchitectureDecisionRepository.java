package com.example.reqsmanager.repository;
import com.example.reqsmanager.entity.ArchitectureDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArchitectureDecisionRepository extends JpaRepository<ArchitectureDecision, Integer>, JpaSpecificationExecutor<ArchitectureDecision> {
}
