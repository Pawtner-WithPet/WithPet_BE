package com.pawtner.WithPet.domain.search.realtime.controller;

import com.pawtner.WithPet.domain.search.realtime.dto.RealTimeSearchStatusResponse;
import com.pawtner.WithPet.domain.search.realtime.entity.RealTimeSearch;
import com.pawtner.WithPet.domain.search.realtime.repository.KeywordMatchResultRepository;
import com.pawtner.WithPet.domain.search.realtime.service.KeywordRealtimeService;
import com.pawtner.WithPet.domain.search.realtime.service.RealTimeSearchService;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/realtime/search")
@RequiredArgsConstructor
public class RealTimeSearchController {

    private final RealTimeSearchService realTimeSearchService;
    private final KeywordRealtimeService keywordRealtimeService;
    private final KeywordMatchResultRepository keywordResultRepo;

    // 켜기 → 키워드 실시간 탐색 실행
    @PostMapping(value = "/start", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> start(@RequestParam Long userId,
                                   @RequestParam Long petId,
                                   @RequestParam String status,           // lost or found
                                   @RequestParam(required = false) String keywords,
                                   @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        byte[] img = (image != null ? image.getBytes() : null);
        RealTimeSearch saved = realTimeSearchService.start(userId, petId, status, keywords);

        try { keywordRealtimeService.runOnce(saved.getId()); } catch (Exception ignore) {}

        return ResponseEntity.ok(
                ApiResponse.success("SUCCESS_REALTIME_START", "실시간 탐색이 활성화되었습니다.",
                RealTimeSearchStatusResponse.from(saved))
        );
    }

    // 끄기
    @PostMapping("/{jobId}/stop")
    public ResponseEntity<?> stop(@PathVariable Long jobId, @RequestParam Long userId) {
        log.warn("[RealtimeStop:CTRL] jobId={}, reqUserId={}", jobId, userId);  // ← warn로
        realTimeSearchService.stop(jobId, userId);
        return ResponseEntity.ok(
                ApiResponse.success("SUCCESS_REALTIME_STOP", "실시간 탐색이 비활성화되었습니다.", null)
        );
    }

    // 실시간 탐색 상태 목록 조회
    @GetMapping("/status")
    public ResponseEntity<?> status(@RequestParam Long userId) {
        List<RealTimeSearchStatusResponse> list = realTimeSearchService.listStatus(userId);
        return ResponseEntity.ok(
                ApiResponse.success("SUCCESS_REALTIME_STATUS", "실시간 탐색 상태 조회 성공", list)
        );
    }

    // 실시간 키워드 탐색 결과 조회
    @GetMapping("/{jobId}/keyword-results")
    public ResponseEntity<?> keywordResults(@PathVariable Long jobId,
                                            @RequestParam(defaultValue = "20") int limit) {
        var job = realTimeSearchService.getJob(jobId);
        var list = keywordResultRepo.findByJobOrderByCreatedAtDesc(
                job, org.springframework.data.domain.PageRequest.of(0, Math.max(1, limit))
        ).stream().map(com.pawtner.WithPet.domain.search.realtime.dto.KeywordMatchResultResponse::from).toList();

        return ResponseEntity.ok(
                ApiResponse.success("SUCCESS_REALTIME_KEYWORD_RESULTS", "실시간 키워드 결과 조회 성공", list)
        );
    }

    // 키워드 탐색 수동 1회 → 스케줄러 기다리지 않고 즉시 실행
    @PostMapping("/{jobId}/keyword-run-once")
    public ResponseEntity<?> keywordRunOnce(@PathVariable Long jobId) {
        int saved = keywordRealtimeService.runOnce(jobId);
        return ResponseEntity.ok(
                ApiResponse.success("SUCCESS_REALTIME_KEYWORD_RUN_ONCE", "키워드 탐색 수동 1회 실행 완료: 저장된 결과 수=" + saved, null)
        );
    }

    // 실행 키워드 조회
    @GetMapping("/keywords")
    public ResponseEntity<?> getLatestKeywords(@RequestParam Long userId,
                                               @RequestParam(defaultValue = "true") boolean activeOnly) {
        var res = realTimeSearchService.getKeywords(userId, activeOnly);
        return ResponseEntity.ok(
                ApiResponse.success("SUCCESS_REALTIME_KEYWORDS_LATEST", "방금 실행된 실시간 탐색의 키워드 조회 성공", res)
        );
    }
}
