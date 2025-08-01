package com.pawtner.WithPet.domain.search.repository;

import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FoundPetPostSpecs {

    public static Specification<FoundPetPost> containsKeywords(List<String> keywords, LocalDateTime lostDate) {

        return (root, query, cb) -> {

            Predicate total = cb.disjunction();

            for (String kw : keywords) {

                if (kw == null || kw.isBlank()) continue; // 빈 키워드 skip

                String pattern = "%" + kw.toLowerCase() + "%";

                Predicate perKeyword = cb.or(
                        cb.like(cb.lower(root.get("kindNm")),       pattern),
                        cb.like(cb.lower(root.get("foundLocation")), pattern),
                        cb.like(cb.lower(root.get("description")),      pattern),
                        cb.like(cb.lower(root.get("sex")),           pattern)
                );

                total = cb.or(total, perKeyword); //키워드 결합 논리 OR: 키워드 하나라도 포함되어 있으면 OK
            }

            // 날짜 조건 (foundDate ≥ lostDate)
            Predicate dateFilter = cb.greaterThanOrEqualTo(root.get("foundDate"), lostDate);

            // 최종 WHERE 절: (모든 키워드 OR)  AND  (날짜 조건)
            return cb.and(total, dateFilter);
        };
    }
}
