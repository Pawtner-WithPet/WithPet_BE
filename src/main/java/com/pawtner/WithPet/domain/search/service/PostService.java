package com.pawtner.WithPet.domain.search.service;

import com.pawtner.WithPet.domain.search.dto.LostPetPostResponse;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import com.pawtner.WithPet.domain.search.repository.LostPetPostRepository;
import com.pawtner.WithPet.domain.search.repository.LostPetPostSpecs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final LostPetPostRepository lostPetPostRepository;

    // [ 실종 동물 목록 조회 ]
    @Transactional(readOnly = true)
    public List<LostPetPostResponse> getLostPets(String keyword) {


        Specification<LostPetPost> spec = LostPetPostSpecs.containsKeyword(keyword);

        List<LostPetPost> posts = lostPetPostRepository.findAll(spec);

        return posts.stream()
                .map(LostPetPostResponse::from)
                .collect(Collectors.toList());
    }

}
