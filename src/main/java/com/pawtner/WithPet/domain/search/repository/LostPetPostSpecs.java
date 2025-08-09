package com.pawtner.WithPet.domain.search.repository;

import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;


@Component
public class LostPetPostSpecs {

    // 키워드 1개 검색 (대상 필드: lostLocation (실종 장소), pet.kindNm (견종), pet.sexNm(성별))
    public static Specification<LostPetPost> containsKeyword(String keyword) {

        return (root, query, cb) -> {

            root.fetch("pet", JoinType.INNER); // 페치 조인 (다건 페치 X, ManyToOne이라 문제 없음)
            query.distinct(true); // DISTINCT로 중복 제거
            query.orderBy(cb.desc(root.get("createdAt"))); // 정렬 (createdAt DESC)


            // 키워드 검색 X
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction(); // 조건 없음 → 전체
            }


            // 키워드 검색 O
            String like = "%" + keyword + "%";
            Join<LostPetPost, Pet> petJoin = root.join("pet", JoinType.INNER); // Join: LostPetPost → Pet (견종 검색을 위해)

            Predicate byLocation = cb.like(root.get("lostLocation"), like);
            Predicate byBreed    = cb.like(petJoin.get("kindNm"), like);
            Predicate bySex      = cb.like(petJoin.get("sexNm"), like);
            return cb.or(byLocation, byBreed, bySex);
        };
    }
}