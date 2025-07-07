package com.pawtner.WithPet.domain.noseprint.repository;

import com.pawtner.WithPet.domain.noseprint.entity.NosePrintSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NosePrintSearchRepository extends JpaRepository<NosePrintSearch, Long> {

    // 비문 결과 목록 조회
    List<NosePrintSearch> findByOwnerIdOrderBySearchDatetimeDesc(Long ownerId);

    // 비문 결과 상세 조회
    Optional<NosePrintSearch> findBySearchId(Long searchId);

}
