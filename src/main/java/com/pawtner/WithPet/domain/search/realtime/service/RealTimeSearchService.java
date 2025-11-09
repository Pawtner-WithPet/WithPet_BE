package com.pawtner.WithPet.domain.search.realtime.service;

import com.pawtner.WithPet.domain.search.realtime.dto.KeywordResponse;
import com.pawtner.WithPet.domain.search.realtime.dto.RealTimeSearchStatusResponse;
import com.pawtner.WithPet.domain.search.realtime.entity.RealTimeSearch;
import com.pawtner.WithPet.domain.search.realtime.repository.RealTimeSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealTimeSearchService {

    private final RealTimeSearchRepository searchRepo;

    // 실시간 탐색 시작
    @Transactional
    public RealTimeSearch start(Long userId, Long petId, String status, String keywords) {
        if (userId == null || petId == null)
            throw new IllegalArgumentException("userId, petId는 필수입니다.");

        if (status == null || (!"lost".equalsIgnoreCase(status) && !"found".equalsIgnoreCase(status)))
            throw new IllegalArgumentException("status는 lost 또는 found여야 합니다.");

        boolean hasKeywords = (keywords != null && !keywords.isBlank());

        if (!hasKeywords) {
            throw new IllegalArgumentException("키워드가 존재해야 합니다.");
        }

        RealTimeSearch entity = searchRepo.findTopByUserIdAndPetIdOrderByUpdatedAtDesc(userId, petId)
                .orElse(RealTimeSearch.builder().userId(userId).petId(petId).build());

        entity.setStatus(status.toLowerCase());
        entity.setKeywords(keywords);
        entity.setActive(true);

        return searchRepo.save(entity);
    }

    @Transactional
    public void stop(Long jobId, Long userId) {
        log.warn("[RealtimeStop:SVC] jobId={}, reqUserId={}", jobId, userId);   // ← warn로

        RealTimeSearch job = searchRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 jobId입니다."));

        if (!job.getUserId().equals(userId))
            throw new SecurityException("UNAUTHORIZED");
        job.setActive(false);
        searchRepo.save(job);
    }

    @Transactional(readOnly = true)
    public List<RealTimeSearchStatusResponse> listStatus(Long userId) {
        return searchRepo.findByUserId(userId).stream().map(RealTimeSearchStatusResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RealTimeSearch getJob(Long jobId) {
        return searchRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 jobId입니다."));
    }

    // 최신 실행 탐색 Job 키워드
    @Transactional(readOnly = true)
    public KeywordResponse getKeywords(Long userId, boolean activeOnly) {
        if (userId == null) throw new IllegalArgumentException("userId는 필수입니다.");

        var opt = activeOnly
                ? searchRepo.findTopByUserIdAndActiveTrueAndLastRunAtIsNotNullOrderByLastRunAtDesc(userId)
                : searchRepo.findTopByUserIdAndLastRunAtIsNotNullOrderByLastRunAtDesc(userId);

        RealTimeSearch job = opt.orElseThrow(() ->
                new IllegalArgumentException("최근 실행된 실시간 탐색이 없습니다. 먼저 탐색을 시작하거나 1회 실행해 주세요."));

        return KeywordResponse.from(job);
    }
}
