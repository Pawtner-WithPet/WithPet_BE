package com.pawtner.WithPet.domain.noseprint.repository;

import com.pawtner.WithPet.domain.noseprint.entity.NoseMatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoseMatchResultRepository extends JpaRepository<NoseMatchResult, Long> {

    // 비문 결과 목록 조회
    List<NoseMatchResult> findByOwnerIdOrderBySearchDatetimeDesc(Long ownerId);

    // 비문 결과 상세 조회
    Optional<NoseMatchResult> findByResultId(Long resultId);

}
