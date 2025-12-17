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

    // 我们覆盖 JpaSpecificationExecutor 的 findAll 方法，并为其添加 @EntityGraph 注解

    /**
     * @EntityGraph 注解告诉 Spring Data JPA 在执行查询时，
     * 使用 'JOIN FETCH' 来立即加载指定的关联属性（这里是 architecturalRequirement）。
     * 这可以有效解决 N+1 查询问题，同时不会影响分页的总数统计。
     */
    @Override
    @EntityGraph(attributePaths = {"architecturalRequirement"})
    Page<Requirement> findAll(Specification<Requirement> spec, Pageable pageable);

    // === START: 修正此处的 JPQL 查询 ===
    /**
     * 按“所属小组”(groupName)分组，统计各项指标。
     */
    @Query("SELECT new com.example.reqsmanager.dto.GroupMetricsDTO(" +
            "r.groupName, " +
            "COUNT(r.id), " +
            // 将 ar.isSummaryDesignSubmitted 改为 ar.summaryDesignSubmitted
            "SUM(CASE WHEN ar.summaryDesignSubmitted = true THEN 1 ELSE 0 END), " +
            "AVG(CASE WHEN ar.summaryDesignScore IS NOT NULL THEN ar.summaryDesignScore ELSE NULL END), " +
            // 将 ar.isDetailedDesignSubmitted 改为 ar.detailedDesignSubmitted
            "SUM(CASE WHEN ar.detailedDesignSubmitted = true THEN 1 ELSE 0 END), " +
            "AVG(CASE WHEN ar.detailedDesignScore IS NOT NULL THEN ar.detailedDesignScore ELSE NULL END)" +
            ") " +
            "FROM Requirement r LEFT JOIN r.architecturalRequirement ar " +
            "WHERE r.groupName IS NOT NULL AND r.groupName != '' " +
            "GROUP BY r.groupName")
    List<GroupMetricsDTO> findGroupMetrics();
    // === END: 修正 ===

    // === START: 新增方法 ===
    /**
     * 根据需求编号检查需求是否存在。
     * Spring Data JPA 会自动根据方法名生成查询。
     * @param reqId 需求编号
     * @return 如果存在则返回 true，否则返回 false
     */
    boolean existsByReqId(String reqId);
    // === END ===
}
