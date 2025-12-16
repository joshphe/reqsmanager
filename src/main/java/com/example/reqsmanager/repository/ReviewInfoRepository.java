package com.example.reqsmanager.repository;

import com.example.reqsmanager.entity.ReviewInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用于操作 ReviewInfo 实体的数据访问接口。
 * 继承 JpaRepository 后，Spring Data JPA 会自动为其提供
 * save(), findById(), findAll(), deleteById() 等标准方法。
 */
@Repository
public interface ReviewInfoRepository extends JpaRepository<ReviewInfo, Integer> {
    // 目前不需要任何自定义的查询方法，继承即可。
    // 未来如果需要根据特定字段查询，可以在这里添加方法，
    // 例如： Optional<ReviewInfo> findByReviewLevel(String level);
}
