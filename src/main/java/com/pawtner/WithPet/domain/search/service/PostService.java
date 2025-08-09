package com.pawtner.WithPet.domain.search.service;

import com.pawtner.WithPet.domain.search.dto.FoundPetPostResponse;
import com.pawtner.WithPet.domain.search.dto.LostPetPostResponse;
import com.pawtner.WithPet.domain.search.dto.PetPostSummaryResponse;
import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import com.pawtner.WithPet.domain.search.repository.FoundPetPostRepository;
import com.pawtner.WithPet.domain.search.repository.FoundPetPostSpecs;
import com.pawtner.WithPet.domain.search.repository.LostPetPostRepository;
import com.pawtner.WithPet.domain.search.repository.LostPetPostSpecs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final LostPetPostRepository lostPetPostRepository;
    private final FoundPetPostRepository foundPetPostRepository;

    // [ 실종 동물 목록 조회 ]
    @Transactional(readOnly = true)
    public List<LostPetPostResponse> getLostPets(String keyword) {


        Specification<LostPetPost> spec = LostPetPostSpecs.containsKeyword(keyword);

        List<LostPetPost> posts = lostPetPostRepository.findAll(spec);

        return posts.stream()
                .map(LostPetPostResponse::from)
                .collect(Collectors.toList());
    }

    // [ 발견 동물 목록 조회 ]
    public List<FoundPetPostResponse> getFoundPets(String keyword) {

        Specification<FoundPetPost> spec = FoundPetPostSpecs.containsKeyword(keyword);

        List<FoundPetPost> posts = foundPetPostRepository.findAll(spec);

        return posts.stream()
                .map(FoundPetPostResponse::from)
                .collect(Collectors.toList());
    }

    // [ 전체 등록 동물 목록 조회 ]
    public List<PetPostSummaryResponse> getAllPets(String keyword) {
        // 1) 각 타입별 조회 (스펙 내부에서 정렬 지정)
        var lostSpec  = LostPetPostSpecs.containsKeyword(keyword);
        var foundSpec = FoundPetPostSpecs.containsKeyword(keyword);

        List<LostPetPost> lost  = lostPetPostRepository.findAll(lostSpec);
        List<FoundPetPost> found = foundPetPostRepository.findAll(foundSpec);

        // 2) 공통 DTO로 매핑
        List<PetPostSummaryResponse> list = new ArrayList<>();
        list.addAll(lost.stream().map(PetPostSummaryResponse::fromLost).collect(Collectors.toList()));
        list.addAll(found.stream().map(PetPostSummaryResponse::fromFound).collect(Collectors.toList()));

        // 3) 통합 정렬 (createdAt DESC) - 안정성 위해 한 번 더 정렬
        list.sort(Comparator.comparing(PetPostSummaryResponse::getCreatedAt).reversed());
        return list;
    }
}
