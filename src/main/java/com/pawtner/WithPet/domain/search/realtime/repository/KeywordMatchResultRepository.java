package com.pawtner.WithPet.domain.search.realtime.repository;

import com.pawtner.WithPet.domain.search.realtime.entity.KeywordMatchResult;
import com.pawtner.WithPet.domain.search.realtime.entity.RealTimeSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface KeywordMatchResultRepository extends JpaRepository<KeywordMatchResult, Long> {
    List<KeywordMatchResult> findByJobOrderByCreatedAtDesc(RealTimeSearch job, Pageable pageable);
}
