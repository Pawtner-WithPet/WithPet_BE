package com.pawtner.WithPet.domain.pet.controller;

import com.pawtner.WithPet.domain.pet.dto.PetDetailResponse;
import com.pawtner.WithPet.domain.pet.dto.PetInfoResponse;
import com.pawtner.WithPet.domain.pet.dto.PetListResponse;
import com.pawtner.WithPet.domain.pet.dto.PetRegisterRequest;
import com.pawtner.WithPet.domain.pet.dto.PetUpdateRequest;
import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.domain.pet.service.PetDBService;
import com.pawtner.WithPet.domain.pet.service.PetService;
import com.pawtner.WithPet.global.dto.ApiErrorResponse;
import com.pawtner.WithPet.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetInfoController {

    private final PetService petService;
    private final PetDBService petDBService;

    // [ 국가동물보호정보시스템 등록 동물 openAPI 연동 test: 지우고 개발하셔도 돼요! - 민주 ]
    @GetMapping("/test")
    public PetInfoResponse getPetInfo(
            @RequestParam String dogRegNo,
            @RequestParam String rfidCd,
            @RequestParam String ownerNm,
            @RequestParam String ownerBirth
    ) throws IOException {
        return petService.getPetInfo(dogRegNo, rfidCd, ownerNm, ownerBirth);
    }

    // 국가동물보호정보시스템 → DB 반려견 등록
    @PostMapping("/register")
    public ResponseEntity<?> registerPet(@RequestBody PetRegisterRequest request) {
        try {
            PetInfoResponse response = petService.getPetInfo(
                    request.getDogRegNo(),
                    request.getRfidCd(),
                    request.getOwnerNm(),
                    request.getOwnerBirth()
            );
            PetInfoResponse.Body.Item item = response.getBody().getItem();
            Pet savedPet = petDBService.savePetInfo(item, request.getUserId());

            PetListResponse petResponse = petDBService.toPetListResponse(savedPet);

            return ResponseEntity.ok(ApiResponse.success("SUCCESS_REGISTER", "반려견 정보를 성공적으로 등록했습니다.", petResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiErrorResponse.of(500, "/api/pet/register", "REGISTER_FAILED", e.getMessage()));
        }
    }

    // 반려견 전체 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> getPetsByUser(@RequestParam Long userId) {
        try {
            List<PetListResponse> pets = petDBService.getPetsByUser(userId);
            return ResponseEntity.ok(ApiResponse.success("SUCCESS_GET_DOGS", "반려견 목록을 성공적으로 조회했습니다.", pets));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiErrorResponse.of(500, "/api/pet/list", "DB_FAILED", e.getMessage()));
        }
    }

    // 반려견 상세 정보 조회
    @GetMapping("/{petId}")
    public ResponseEntity<?> getPetDetailInfo(@PathVariable Long petId){
        PetDetailResponse petData = petDBService.getPetDetailInfo(petId);;

        ApiResponse<PetDetailResponse> response = ApiResponse.success("SUCCESS_GET_DOG_DETAIL", "반려견 상세 정보를 조회했습니다.", petData);

        return ResponseEntity.ok(response);
    }
    
    // 반려견 정보 수정
    @PatchMapping("/{petId}")
    public ResponseEntity<?> updatePetInfo(@PathVariable Long petId, @RequestBody PetUpdateRequest request) {
        try {
            Pet updatedPet = petDBService.updatePetInfo(request.getUserId(), petId, request.getFeatures());
            PetDetailResponse petResponse = petDBService.getPetDetailInfo(petId);

            return ResponseEntity.ok(ApiResponse.success("SUCCESS_UPDATE_DOG_DETAIL", "반려견 정보를 수정했습니다.", petResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiErrorResponse.of(404, "/api/pet" + petId, "PET_NOT_FOUND", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiErrorResponse.of(403, "/api/pet/" + petId, "UNAUTHORIZED", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiErrorResponse.of(500, "/api/pet/" + petId, "UPDATE_FAILED", e.getMessage()));
        }
    }
}
