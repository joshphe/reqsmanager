package com.example.reqsmanager.service;
import com.example.reqsmanager.entity.ArchitectureDecision;
import com.example.reqsmanager.repository.ArchitectureDecisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ArchitectureDecisionService {
    @Autowired private ArchitectureDecisionRepository decisionRepository;

    public Page<ArchitectureDecision> findDecisions(String reqId, Pageable pageable) {
        Specification<ArchitectureDecision> spec = (root, query, cb) -> {
            if (reqId != null && !reqId.isEmpty()) {
                return cb.like(root.get("reqId"), "%" + reqId + "%");
            }
            return cb.conjunction();
        };
        return decisionRepository.findAll(spec, pageable);
    }
    public ArchitectureDecision findById(Integer id) {
        return decisionRepository.findById(id).orElseThrow(() -> new RuntimeException("Decision not found"));
    }
    public void save(ArchitectureDecision decision) {
        decisionRepository.save(decision);
    }
    public void deleteById(Integer id) {
        decisionRepository.deleteById(id);
    }
}
