package com.example.reqsmanager.service;

import com.example.reqsmanager.dto.ReviewInfoDTO;
import com.example.reqsmanager.entity.ReviewInfo;
import com.example.reqsmanager.repository.ReviewInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewInfoService {

    @Autowired
    private ReviewInfoRepository reviewInfoRepository;

    @Transactional
    public ReviewInfo saveReviewInfo(ReviewInfoDTO dto) {
        ReviewInfo reviewInfo = reviewInfoRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid review info Id: " + dto.getId()));

        // 从 DTO 完整地复制所有字段到实体
        reviewInfo.setReviewCheck1(dto.getReviewCheck1());
        reviewInfo.setReviewCheck2(dto.getReviewCheck2());
        reviewInfo.setReviewCheck3(dto.getReviewCheck3());
        reviewInfo.setReviewCheck4(dto.getReviewCheck4());
        reviewInfo.setReviewCheck5(dto.getReviewCheck5());
        reviewInfo.setReviewCheck6(dto.getReviewCheck6());
        reviewInfo.setReviewCheck7(dto.getReviewCheck7());
        reviewInfo.setReviewCheck8(dto.getReviewCheck8());
        reviewInfo.setReviewCheck9(dto.getReviewCheck9());
        reviewInfo.setReviewLevel(dto.getReviewLevel());

        reviewInfo.setAuditCheck1(dto.getAuditCheck1());
        reviewInfo.setAuditCheck2(dto.getAuditCheck2());
        reviewInfo.setAuditCheck3(dto.getAuditCheck3());
        reviewInfo.setAuditCheck4(dto.getAuditCheck4());
        reviewInfo.setAuditCheck5(dto.getAuditCheck5());
        reviewInfo.setAuditCheck6(dto.getAuditCheck6());
        reviewInfo.setAuditCheck7(dto.getAuditCheck7());
        reviewInfo.setAuditCheck8(dto.getAuditCheck8());
        reviewInfo.setAuditCheck9(dto.getAuditCheck9());
        reviewInfo.setAuditLevel(dto.getAuditLevel());

        return reviewInfoRepository.save(reviewInfo);
    }
}
