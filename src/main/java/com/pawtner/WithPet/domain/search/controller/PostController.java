package com.pawtner.WithPet.domain.search.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawtner.WithPet.domain.search.dto.*;
import com.pawtner.WithPet.domain.search.service.PostService;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?>completeFoundPost(@PathVariable Long postId) {
        postService.completeFoundPost(postId);

        ApiResponse<?> response = ApiResponse.success("SUCCESS_COMPLETE_FOUND_POST", "발견글이 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

    // [ 실종글 완료 - 삭제, 미실종 처리 ]
    @DeleteMapping("/done/lost-post/{postId}")
    public ResponseEntity<?>completeLostPost(@PathVariable Long postId) {
        postService.completeLostPost(postId);

        ApiResponse<?> response = ApiResponse.success("SUCCESS_COMPLETE_LOST_POST", "실종글을 삭제하고 반려견 상태를 미실종으로 복구했습니다.");
        return ResponseEntity.ok(response);
    }

    // 발견동물 등록
    @PostMapping("/foundPost")
    public ResponseEntity<?> uploadFoundPost(
            @RequestPart("request") String request,
            @RequestPart(value = "image", required = false) MultipartFile image ) throws JsonProcessingException {

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            FoundPostRequest foundPostRequest = mapper.readValue(request, FoundPostRequest.class);

            ApiResponse<String> result = postService.uploadFoundPost(foundPostRequest, image);
            return ResponseEntity.ok(result);

        } catch (JsonProcessingException e) {
            ApiResponse<String> result = ApiResponse.error("INVALID_JSON_FORMAT", "잘못된 요청 형식입니다.", 400);
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            ApiResponse<String> result = ApiResponse.error("INTERNAL_SERVER_ERROR", "게시글 업로드 중 오류가 발생했습니다.", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // 실종동물 등록
    @PostMapping("/lostPost")
    public ResponseEntity<?> uploadLostPost(
            @RequestPart("request") String request,
            @RequestPart(value = "image", required = false) MultipartFile image ) throws JsonProcessingException {

        try {
            // 요청 값 검증
            if (request == null || request.isBlank()) {
                return ResponseEntity.badRequest().body("요청 데이터가 비어 있습니다.");
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            LostPostRequest lostPostRequest = mapper.readValue(request, LostPostRequest.class);

            if (lostPostRequest == null) {
                return ResponseEntity.badRequest().body("요청 데이터 매핑에 실패했습니다.");
            }

            ApiResponse<String> result = postService.uploadLostPost(lostPostRequest, image);
            return ResponseEntity.ok(result);

        } catch (JsonProcessingException e) {
            ApiResponse<String> result = ApiResponse.error("INVALID_JSON_FORMAT", "잘못된 요청 형식입니다.", 400);
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            ApiResponse<String> result = ApiResponse.error("INTERNAL_SERVER_ERROR", "게시글 업로드 중 오류가 발생했습니다.", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

}
