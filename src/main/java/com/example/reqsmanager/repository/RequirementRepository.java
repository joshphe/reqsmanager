package com.example.reqsmanager.repository;

import com.example.reqsmanager.dto.GroupMetricsDTO;
import com.example.reqsmanager.entity.Requirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Integer>, JpaSpecificationExecutor<Requirement> {

    @Override
    @EntityGraph(attributePaths = {"architecturalRequirement", "architecturalRequirement.reviewInfo"})
    Page<Requirement> findAll(Specification<Requirement> spec, Pageable pageable);

    @Query("SELECT new com.example.reqsmanager.dto.GroupMetricsDTO(" +
            "r.groupName, " +
            "COUNT(r.id), " +
            "SUM(CASE WHEN ar.summaryDesignSubmitted = true THEN 1 ELSE 0 END), " +
            "AVG(CASE WHEN ar.summaryDesignScore IS NOT NULL THEN ar.summaryDesignScore ELSE NULL END)" +
            ") " +
            "FROM Requirement r LEFT JOIN r.architecturalRequirement ar " +
            "WHERE r.groupName IS NOT NULL AND r.groupName != '' " +
            "GROUP BY r.groupName")
    List<GroupMetricsDTO> findGroupMetrics();

    // === START: 新增这个缺失的方法 ===
    /**
     * 根据需求编号检查需求是否存在。
     * Spring Data JPA 会自动根据方法名生成 "SELECT COUNT(*) FROM requirements WHERE req_id = ?" 查询。
     * @param reqId 需求编号
     * @return 如果存在则返回 true，否则返回 false
     */
    boolean existsByReqId(String reqId);
    // === END ===
}
