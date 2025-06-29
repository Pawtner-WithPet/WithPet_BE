package com.pawtner.WithPet.domain.noseprint.controller;

import com.pawtner.WithPet.domain.noseprint.dto.NosePrintDetailResponse;
import com.pawtner.WithPet.domain.noseprint.entity.NosePrint;
import com.pawtner.WithPet.domain.noseprint.service.NosePrintService;
import com.pawtner.WithPet.domain.pet.dto.PetListResponse;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/noseprint")
@RequiredArgsConstructor
public class NosePrintController {

    private final NosePrintService nosePrintService;

    // [ 비문 이미지 등록 ]
    @PostMapping("/image")
    public ResponseEntity<?> registerNosePrint(
            @RequestParam MultipartFile nosePrintImg,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long ownerId
    ) {
        // 이미지 저장 & 엔티티 생성
        NosePrint saved = nosePrintService.saveNosePrint(nosePrintImg, petId, ownerId);
        // 등록한 비문 상세 정보 반환
        NosePrintDetailResponse nosePrintResponse = nosePrintService.getNosePrintDetailInfo(saved.getNose_print_id());

        return ResponseEntity.ok(ApiResponse.success("SUCCESS_UPLOAD_NOSEPRINT", "비문 이미지를 성공적으로 등록했습니다.", nosePrintResponse));
    }

    // 비문 등록 반려견 조회
    @GetMapping("/pets")
    public ResponseEntity<ApiResponse<List<PetListResponse>>> getPetsWithNosePrint(@RequestParam Long userId) {
        return ResponseEntity.ok(nosePrintService.getPetsWithNosePrint(userId));
    }
}
