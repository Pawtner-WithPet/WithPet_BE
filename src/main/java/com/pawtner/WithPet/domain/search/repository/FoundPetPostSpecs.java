package com.pawtner.WithPet.domain.search.repository;

import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import com.pawtner.WithPet.global.enums.Sex;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class FoundPetPostSpecs {

    // [ 서비스 내 발견동물 기반 탐색 - 키워드 여러개 ]
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


    // [ 발견 동물 목록 조회 - 키워드 1개 검색 ]
    public static Specification<FoundPetPost> containsKeyword(String keyword) {

        return (root, query, cb) -> {

            query.orderBy(cb.desc(root.get("createdAt"))); // 정렬 (createdAt DESC)

            // 키워드 검색 X
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }


            // 키워드 검색 O
            String like = "%" + keyword + "%";
            List<Predicate> ors = new ArrayList<>();

            // 장소/견종 LIKE
            ors.add(cb.like(root.get("foundLocation"), like));
            ors.add(cb.like(root.get("kindNm"), like));

            // 성별 (Enum 정확 일치만 허용: male로 검색 시 female도 포함되는 문제 해결)
            Sex mapped = mapToSex(keyword);
            if (mapped != null) {
                ors.add(cb.equal(root.get("sex"), mapped));
            }


            return cb.or(ors.toArray(new Predicate[0]));
        };
    }

    // ( 암컷/수컷/미상 등 다양한 표현을 Enum으로 안전 매핑 )
    private static Sex mapToSex(String raw) {
        if (raw == null) return null;
        String k = raw.trim().toLowerCase();
        switch (k) {
            // FEMALE
            case "암컷":
            case "여":
            case "여아":
            case "female":
                return Sex.FEMALE;
            // MALE
            case "수컷":
            case "숫컷":
            case "남":
            case "남아":
            case "male":
                return Sex.MALE;
            // UNKNOWN
            case "미상":
            case "모름":
            case "unknown":
                return Sex.UNKNOWN;
            default:
                return null;
        }
    }
}
