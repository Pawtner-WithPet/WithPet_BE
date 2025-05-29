package com.pawtner.WithPet.domain.pet.controller;

import com.pawtner.WithPet.domain.pet.dto.PetInfoResponse;
import com.pawtner.WithPet.domain.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetInfoController {

    private final PetService petService;

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
}
