package com.pawtner.WithPet.domain.search.service;

import com.pawtner.WithPet.domain.search.dto.FoundPetPostResponse;
import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import com.pawtner.WithPet.domain.search.exception.InvalidSearchKeywordException;
import com.pawtner.WithPet.domain.search.exception.LostPetPostNotFoundException;
import com.pawtner.WithPet.domain.search.repository.FoundPetPostRepository;
import com.pawtner.WithPet.domain.search.repository.LostPetPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.pawtner.WithPet.domain.search.repository.FoundPetPostSpecs.containsKeywords;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final FoundPetPostRepository foundPetPostRepository;
    private final LostPetPostRepository lostPetPostRepository;

    // [ 발견동물 기반 탐색 ]
    public List<FoundPetPostResponse> searchFoundPets(Long petId, List<String> keywords) {

        log.info("[SVC] START searchFoundPets petId={}, keywords={}", petId, keywords); // 로그 2


        // 키워드 유효성 검사
        if (keywords == null || keywords.stream().allMatch(k -> k == null || k.trim().isEmpty())) {
            throw new InvalidSearchKeywordException("키워드를 1개 이상 입력해야 합니다.");
        }
        log.info("[SVC] keywords validation OK"); // 로그 3

        // 실종글 조회 (petId → lostDate)
        LostPetPost lost = lostPetPostRepository.findTopByPetIdOrderByLostDateDesc(petId)
                .orElseThrow(() -> new LostPetPostNotFoundException("해당 petId 로 등록된 실종 글이 없습니다. petId=" + petId));

        LocalDateTime lostDate = lost.getLostDate();
        log.info("[SVC] lostDate={}", lost.getLostDate()); // 로그 4

        // Specification 생성
        Specification<FoundPetPost> spec = containsKeywords(keywords, lostDate);

        // 발견글 조회 실행
        List<FoundPetPost> entities = foundPetPostRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "foundDate"));

        // 디버그용 매칭 키워드 로그
        for (FoundPetPost fp : entities) {
            List<String> hit = keywords.stream()
                    .filter(k -> !k.isBlank())
                    .filter(k -> isKeywordMatched(fp, k))
                    .toList();
            log.info("[MATCH] postId={} -> {}", fp.getFoundPostId(), hit); // 로그 5
        }

        // DTO 매핑 후 반환
        return entities.stream()
                .map(FoundPetPostResponse::from)
                .toList();

    }
    // 개발 로그용)특정 발견글이 키워드를 포함하는지 여부
    private boolean isKeywordMatched(FoundPetPost fp, String keyword) {
        String p = keyword.toLowerCase();
        return Optional.ofNullable(fp.getKindNm()).map(String::toLowerCase).orElse("").contains(p)
                || Optional.ofNullable(fp.getFoundLocation()).map(String::toLowerCase).orElse("").contains(p)
                || Optional.ofNullable(fp.getDescription()).map(String::toLowerCase).orElse("").contains(p);
    }


}
