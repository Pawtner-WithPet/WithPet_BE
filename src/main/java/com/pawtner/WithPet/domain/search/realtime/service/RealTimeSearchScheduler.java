package com.pawtner.WithPet.domain.search.realtime.service;

import com.pawtner.WithPet.domain.search.realtime.entity.RealTimeSearch;
import com.pawtner.WithPet.domain.search.realtime.repository.RealTimeSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealTimeSearchScheduler {

    private final RealTimeSearchRepository searchRepo;
    private final KeywordRealtimeService keywordRealtimeService;

    @Scheduled(fixedDelayString = "${app.realtime.interval-ms:60000}")
    public void poll() {
        List<RealTimeSearch> jobs = searchRepo.findByActiveTrue();
        for (RealTimeSearch job : jobs) {
            try {
                int saved = keywordRealtimeService.runOnce(job.getId());
                log.info("[Realtime] jobId={}, savedResults={}", job.getId(), saved);
            } catch (Exception e) {
                log.warn("[Realtime] jobId={} run error: {}", job.getId(), e.getMessage());
            }
        }
    }
}