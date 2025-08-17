package com.pawtner.WithPet.domain.search.service;

import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.domain.pet.repository.PetRepository;
import com.pawtner.WithPet.domain.search.dto.*;
import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import com.pawtner.WithPet.domain.search.exception.FoundPetPostNotFoundException;
import com.pawtner.WithPet.domain.search.exception.InvalidPostTypeException;
import com.pawtner.WithPet.domain.search.exception.LostPetPostNotFoundException;
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
    private final PetRepository petRepository;

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

    // [ 게시물 상세 조회 ]
    public Object getDetail(Long postId, String type) {
        if (postId == null || type == null || type.isBlank()) {
            throw new IllegalArgumentException("postId와 type은 필수입니다.");
        }

        String t = type.trim().toUpperCase();

        switch (t) {
            case "LOST": {
                LostPetPost l = lostPetPostRepository.findWithAllByLostPostId(postId)
                        .orElseThrow(() -> new LostPetPostNotFoundException("해당 실종글을 찾을 수 없습니다. postId=" + postId));
                return LostPetPostDetailResponse.from(l);
            }
            case "FOUND": {
                FoundPetPost f = foundPetPostRepository.findWithAllByFoundPostId(postId)
                        .orElseThrow(() -> new FoundPetPostNotFoundException("해당  발견글을 찾을 수 없습니다. postId=" + postId));
                return FoundPetPostDetailResponse.from(f);
            }
            default:
                throw new InvalidPostTypeException("유효하지 않은 type: " + type + " (LOST|FOUND 만 허용)");
        }
    }


    // [ 발견글 완료 - 삭제 ]
    public void completeFoundPost(Long postId) {
        FoundPetPost post = foundPetPostRepository.findById(postId)
                .orElseThrow(() -> new FoundPetPostNotFoundException("해당  발견글을 찾을 수 없습니다. postId=" + postId));

        foundPetPostRepository.delete(post);
    }

    // [ 실종글 완료 - 삭제, 미실종 처리 ]
    @Transactional
    public void completeLostPost(Long postId) {
        LostPetPost post = lostPetPostRepository.findWithPetByLostPostId(postId)
                .orElseThrow(() -> new LostPetPostNotFoundException("해당  실종글을 찾을 수 없습니다. postId=" + postId));

        // PET-STATUS: 실종(t) -> 미실종(f)
        Pet pet = post.getPet();
        petRepository.updateStatusById(pet.getId(), true);

        // 실종글 삭제
        lostPetPostRepository.delete(post);
    }
}
