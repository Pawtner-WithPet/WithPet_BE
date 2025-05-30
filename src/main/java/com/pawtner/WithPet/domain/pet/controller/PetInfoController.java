package com.pawtner.WithPet.domain.pet.controller;

import com.pawtner.WithPet.domain.pet.dto.PetInfoResponse;
import com.pawtner.WithPet.domain.pet.dto.PetListResponse;
import com.pawtner.WithPet.domain.pet.dto.PetRegisterRequest;
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
}
