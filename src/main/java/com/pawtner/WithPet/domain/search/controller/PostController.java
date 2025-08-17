package com.pawtner.WithPet.domain.search.controller;

import com.pawtner.WithPet.domain.search.dto.FoundPetPostResponse;
import com.pawtner.WithPet.domain.search.dto.LostPetPostResponse;
import com.pawtner.WithPet.domain.search.dto.PetPostSummaryResponse;
import com.pawtner.WithPet.domain.search.service.PostService;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    // [ 실종 동물 목록 조회 ]
    @GetMapping("/lost-list")
    public ResponseEntity<?> getLostPets(@RequestParam(required = false) String keyword) {

        List<LostPetPostResponse> result = postService.getLostPets(keyword);

        ApiResponse<List<LostPetPostResponse>> response = ApiResponse.success("SUCCESS_GET_LOST_LIST", "실종 동물 목록을 성공적으로 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }


    // [ 발견 동물 목록 조회 ]
    @GetMapping("/found-list")
    public ResponseEntity<?> getFoundPets(@RequestParam(required = false) String keyword) {

        List<FoundPetPostResponse> result = postService.getFoundPets(keyword);

        ApiResponse<List<FoundPetPostResponse>> response = ApiResponse.success("SUCCESS_GET_FOUND_LIST", "발견 동물 목록을 성공적으로 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }


    // [ 전체 등록 동물 목록 조회 ]
    @GetMapping("/all-list")
    public ResponseEntity<?> getAllPets(@RequestParam(required = false) String keyword) {

        List<PetPostSummaryResponse> result = postService.getAllPets(keyword);

        ApiResponse<List<PetPostSummaryResponse>> response = ApiResponse.success("SUCCESS_GET_ALL_LIST", "전체 등록 동물 목록을 성공적으로 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }


    // [ 게시물 상세 조회 ]
    @GetMapping("/detail")
    public ResponseEntity<?> getDetail(@RequestParam Long postId, @RequestParam String type) {
        Object payload = postService.getDetail(postId, type);
        ApiResponse<?> response = ApiResponse.success(
                "SUCCESS_GET_DETAIL", "상세 정보를 성공적으로 조회했습니다.", payload);
        return ResponseEntity.ok(response);
    }

    // [ 발견글 완료 - 삭제 ]
    @DeleteMapping("/done/found-post/{postId}")
    public ResponseEntity<?>deleteFoundPost(@PathVariable Long postId) {
        postService.deleteFoundPost(postId);

        ApiResponse<?> response = ApiResponse.success("SUCCESS_DELETE_FOUND_POST", "발견글이 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

}
