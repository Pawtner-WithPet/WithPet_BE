package com.pawtner.WithPet.domain.search.controller;

import com.pawtner.WithPet.domain.search.dto.FoundPetPostResponse;
import com.pawtner.WithPet.domain.search.dto.LostPetPostResponse;
import com.pawtner.WithPet.domain.search.service.PostService;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    // [ 실종동물 목록 조회 ]
    @GetMapping("/lost-list")
    public ResponseEntity<?> getLostPets(@RequestParam(required = false) String keyword) {

        List<LostPetPostResponse> result = postService.getLostPets(keyword);

        ApiResponse<List<LostPetPostResponse>> response = ApiResponse.success("SUCCESS_GET_LOST_LIST", "실종 동물 목록을 성공적으로 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }

    // [ 발견동물 목록 조회 ]
    @GetMapping("/found-list")
    public ResponseEntity<?> getFoundPets(@RequestParam(required = false) String keyword) {

        List<FoundPetPostResponse> result = postService.getFoundPets(keyword);

        ApiResponse<List<FoundPetPostResponse>> response = ApiResponse.success("SUCCESS_GET_FOUND_LIST", "발견 동물 목록을 성공적으로 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }
}
