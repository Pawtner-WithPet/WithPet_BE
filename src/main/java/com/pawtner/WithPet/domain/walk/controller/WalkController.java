package com.pawtner.WithPet.domain.walk.controller;

import com.pawtner.WithPet.domain.walk.dto.LostLocationPostResponse;
import com.pawtner.WithPet.domain.walk.dto.LostLocationResponse;
import com.pawtner.WithPet.domain.walk.dto.WalkRequestDto;
import com.pawtner.WithPet.domain.walk.service.WalkService;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/walk")
@RequiredArgsConstructor
@Slf4j
public class WalkController {

    private final WalkService walkService;

    // 실종견 위치 조회
    @GetMapping("/lostLocation")
    public ResponseEntity<?> getLostLocation(@RequestParam String latitude, @RequestParam String longitude) {

        List<LostLocationResponse> result = walkService.getLostLocation(latitude, longitude);

        ApiResponse<List<LostLocationResponse>> response = ApiResponse.success("SUCCESS_GET_LOST_LOCATION", "근처 실종견 위치를 성공적으로 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }

    // 실종견 게시글 이동
    @GetMapping("/lostWalk")
    public ResponseEntity<?> getLostWalk(@RequestParam Long postId) {

        LostLocationPostResponse result = walkService.getLostWalk(postId);

        ApiResponse<LostLocationPostResponse> response = ApiResponse.success("SUCCESS_GET_LOST_LOCATION", "특정 실종견 실종 게시글 값을 성공적으로 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }

    // 산책 기록 저장
    @PostMapping("/save")
    public ResponseEntity<?> saveWalk(@RequestBody WalkRequestDto request){
        Long walkId = walkService.saveWalk(request);

        ApiResponse<Long> response = ApiResponse.success("SUCCESS_SAVE_WALK", "산책을 성공적으로 기록했습니다.", walkId);

        return ResponseEntity.ok(response);
    }
}