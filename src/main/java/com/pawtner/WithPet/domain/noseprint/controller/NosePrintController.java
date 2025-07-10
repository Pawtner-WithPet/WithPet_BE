package com.pawtner.WithPet.domain.noseprint.controller;

import com.pawtner.WithPet.domain.noseprint.dto.NosePrintDetailResponse;
import com.pawtner.WithPet.domain.noseprint.dto.NosePrintImageResponse;
import com.pawtner.WithPet.domain.noseprint.dto.NosePrintListDetailResponse;
import com.pawtner.WithPet.domain.noseprint.dto.NosePrintListResponse;
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


    // [ 비문 등록 반려견 조회 ]
    @GetMapping("/pets")
    public ResponseEntity<ApiResponse<List<PetListResponse>>> getPetsWithNosePrint(@RequestParam Long userId) {
        return ResponseEntity.ok(nosePrintService.getPetsWithNosePrint(userId));
    }


    // [ 특정 반려견 비문 이미지 조회 ]
    @GetMapping("/{petId}")
    public ResponseEntity<ApiResponse<NosePrintImageResponse>> getNosePrintByPetId(
            @PathVariable Long petId,
            @RequestParam Long userId // 로그인 기능 개발 전까지 임시로 전달받음
    ) {
        return ResponseEntity.ok(nosePrintService.getNosePrintByPetId(petId, userId));
    }

    // [비문 결과 목록 조회]
    @GetMapping("/list")
    public ResponseEntity<?> getNosePrintList(@RequestParam Long ownerId){
        List<NosePrintListResponse> result = nosePrintService.getNosePrintList(ownerId);

        ApiResponse<List<NosePrintListResponse>> response = ApiResponse.success("SUCCESS_GET_NOSEPRINT_LIST", "비문 탐색 결과 목록을 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }

    // [비문 결과 상세 조회]
    @GetMapping("/list/{searchId}")
    public ResponseEntity<?> getNosePrintListDetail(@PathVariable Long searchId){
        NosePrintListDetailResponse result = nosePrintService.getNosePrintListDetail(searchId);

        ApiResponse<NosePrintListDetailResponse> response = ApiResponse.success("SUCCESS_GET_NOSEPRINT_LIST", "비문 탐색 결과 상세 정보를 조회했습니다.", result);

        return ResponseEntity.ok(response);
    }


    // [ 특정 반려견 비문 이미지 수정 ]
    @PutMapping("/image/{petId}")
    public ResponseEntity<ApiResponse<NosePrintDetailResponse>> updateNosePrintImage(
            @PathVariable Long petId,
            @RequestParam MultipartFile nosePrintImg,
            @RequestParam Long userId // 로그인 기능 도입 전까지 임시 전달
    ) {
        NosePrintDetailResponse result = nosePrintService.updateNosePrintImage(petId, userId, nosePrintImg);

        return ResponseEntity.ok(ApiResponse.success("SUCCESS_UPDATE_NOSEPRINT", "비문 이미지를 성공적으로 수정했습니다.", result));
    }


}
