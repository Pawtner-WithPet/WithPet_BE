package com.pawtner.WithPet.domain.search.realtime.service;

import com.pawtner.WithPet.domain.search.dto.FoundPetPostResponse;
import com.pawtner.WithPet.domain.search.dto.ShelterSearchResponse;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import com.pawtner.WithPet.domain.search.realtime.entity.*;
import com.pawtner.WithPet.domain.search.realtime.repository.*;
import com.pawtner.WithPet.domain.search.realtime.support.KeywordUtils;
import com.pawtner.WithPet.domain.search.repository.LostPetPostRepository;
import com.pawtner.WithPet.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordRealtimeService {

    private final RealTimeSearchRepository jobRepo;
    private final LostPetPostRepository lostRepo;
    private final SearchService searchService;
    private final SnsPostRepository snsRepo;
    private final KeywordMatchResultRepository resultRepo;

    @Value("${app.realtime.keyword.min-matches:1}")
    private int minMatches;

    @Transactional
    public int runOnce(Long jobId) {
        RealTimeSearch job = jobRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("job not found: " + jobId));
        if (!job.isActive()) return 0;
        if (!"lost".equalsIgnoreCase(job.getStatus())) return 0;

        LostPetPost lost = lostRepo.findTopByPetIdOrderByLostDateDesc(job.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("해당 petId의 실종글이 없습니다. petId: " + job.getPetId()));
        LocalDateTime since = lost.getLostDate();

        List<String> kws = KeywordUtils.parse(job.getKeywords());
        if (kws.isEmpty()) return 0;

        int saved = 0;
        saved += scanFound(job, kws, since);
        saved += scanShelter(job, kws, since);
        saved += scanSns(job, kws, since);

        try { job.setLastRunAt(LocalDateTime.now()); } catch (Exception ignore) {}
        return saved;
    }

    private int scanFound(RealTimeSearch job, List<String> kws, LocalDateTime since) {
        List<FoundPetPostResponse> list = searchService.searchFoundPets(job.getPetId(), kws);
        int cnt = 0;
        for (FoundPetPostResponse f : list) {
            if (f.getFoundDate()!=null && f.getFoundDate().isBefore(since)) continue;
            String hay = ((s(f.getFoundLocation()) + " " + s(f.getKindNm()) + " " + s(f.getSex())).trim());
            var matched = KeywordUtils.matched(hay, kws);
            if (matched.size() < minMatches) continue;

            KeywordMatchResult r = KeywordMatchResult.builder()
                    .job(job).sourceType("FOUND")
                    .refKey("FOUND:" + f.getPostId())
                    .title("발견동물")
                    .imageUrl(f.getImgUrl())
                    .location(f.getFoundLocation())
                    .text(null)
                    .eventDate(f.getFoundDate())
                    .matchedKeywords(String.join(", ", matched))
                    .matchedCount(matched.size())
                    .build();
            try { resultRepo.save(r); cnt++; } catch (Exception ignore) {}
        }
        return cnt;
    }

    private int scanShelter(RealTimeSearch job, List<String> kws, LocalDateTime since) {
        List<ShelterSearchResponse> list = searchService.getShelterSearch(job.getPetId(), kws);
        int cnt = 0;
        for (ShelterSearchResponse s : list) {
            String hay = ((s(s.getFeature()) + " " + s(s.getFoundLocation()) + " " + s(s.getShelterLocation())).trim());
            var matched = KeywordUtils.matched(hay, kws);
            if (matched.size() < minMatches) continue;

            String refKey = "SHELTER:" + (s(s.getPetImg()) + "|" + s(s.getShelterName()) + "|" + s(s.getFoundLocation()));
            KeywordMatchResult r = KeywordMatchResult.builder()
                    .job(job).sourceType("SHELTER").refKey(refKey)
                    .title(s.getShelterName()).imageUrl(s.getPetImg())
                    .location(s.getFoundLocation()).text(s.getFeature())
                    .eventDate(null)
                    .matchedKeywords(String.join(", ", matched)).matchedCount(matched.size())
                    .build();
            try { resultRepo.save(r); cnt++; } catch (Exception ignore) {}
        }
        return cnt;
    }

    private int scanSns(RealTimeSearch job, List<String> kws, LocalDateTime since) {
        var list = snsRepo.findByPostedAtAfterOrderByPostedAtAsc(since);
        int cnt = 0;
        for (SnsPost p : list) {
            String hay = ((s(p.getText()) + " " + s(p.getRegion())).trim());
            var matched = KeywordUtils.matched(hay, kws);
            if (matched.size() < minMatches) continue;

            KeywordMatchResult r = KeywordMatchResult.builder()
                    .job(job).sourceType("SNS").refKey("SNS:" + p.getId())
                    .title(p.getPlatform()).imageUrl(p.getImageUrl())
                    .location(p.getRegion()).text(p.getText())
                    .eventDate(p.getPostedAt())
                    .matchedKeywords(String.join(", ", matched)).matchedCount(matched.size())
                    .build();
            try { resultRepo.save(r); cnt++; } catch (Exception ignore) {}
        }
        return cnt;
    }

    private static String s(Object o){ return o==null? "" : String.valueOf(o); }
}
