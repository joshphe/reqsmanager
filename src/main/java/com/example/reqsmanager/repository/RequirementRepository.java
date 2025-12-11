package com.example.reqsmanager.repository;

import com.example.reqsmanager.entity.Requirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

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

}
