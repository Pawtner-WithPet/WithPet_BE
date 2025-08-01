package com.pawtner.WithPet.domain.search.controller;

import com.pawtner.WithPet.domain.search.dto.FoundPetPostResponse;
import com.pawtner.WithPet.domain.search.service.SearchService;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

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

}
