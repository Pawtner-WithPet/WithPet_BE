package com.pawtner.WithPet.domain.search.controller;

import com.pawtner.WithPet.domain.search.dto.FoundPetPostResponse;
import com.pawtner.WithPet.domain.search.dto.LostStatusPetResponse;
import com.pawtner.WithPet.domain.search.dto.ShelterSearchResponse;
import com.pawtner.WithPet.domain.search.service.SearchService;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

    // [ 사용자별 실종 반려견 목록 ]
    @GetMapping("/{userId}/pets/lost")
    public ResponseEntity<?> getUserLostPets(@PathVariable Long userId) {
        List<LostStatusPetResponse> result = searchService.getLostPetsByUser(userId);

        ApiResponse<List<LostStatusPetResponse>> response = ApiResponse.success("SUCCESS_GET_USER_LOST_PETS", "사용자의 실종 상태 반려견 목록을 성공적으로 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }

    // [ 발견동물 기반 탐색 ]
    @GetMapping("/withfoundpet")
    public ResponseEntity<?> getFoundPetSearch(@RequestParam Long petId, @RequestParam String keywords) {
        log.info("[CTRL] petId={}, keywords={}", petId, keywords); // 로그 1

        List<String> keywordList = Arrays.stream(keywords.split("[ ,]+"))
                .filter(s -> !s.isBlank())
                .toList();

        List<FoundPetPostResponse> result = searchService.searchFoundPets(petId, keywordList);

        ApiResponse<List<FoundPetPostResponse>> response = ApiResponse.success("SUCCESS_SEARCH_WITH_FOUND_PET", "발견동물 기반 탐색 결과를 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }

    // [ 보호소 기반 탐색 ]
    @GetMapping("/withshelter")
    public ResponseEntity<?> getShelterSearch(@RequestParam Long petId, @RequestParam(required = false) String keywords){
        List<String> keywordList = Arrays.stream(keywords.split("[ ,]+"))
                .filter(s -> !s.isBlank())
                .toList();

        List<ShelterSearchResponse> result = searchService.getShelterSearch(petId, keywordList);

        ApiResponse<List<ShelterSearchResponse>> response = ApiResponse.success("SUCCESS_SEARCH_WITH_SHELTER", "보호소 기반 탐색 결과를 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }
}
