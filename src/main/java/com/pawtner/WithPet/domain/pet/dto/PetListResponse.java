package com.pawtner.WithPet.domain.pet.dto;

import com.pawtner.WithPet.domain.pet.entity.Pet;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class PetListResponse {
    private Long id;
    private String dogRegNo;
    private String dogNm;
    private int dogAge;
    private String kindNm;
    private String sexNm;
    private String dogImg;
    private String nosePrintImg;

    public static PetListResponse fromEntity(Pet pet, String nosePrintImg) {
        return PetListResponse.builder()
                .id(pet.getId())
                .dogRegNo(pet.getDogRegNo())
                .dogNm(pet.getDogNm())
                .kindNm(pet.getKindNm())
                .sexNm(pet.getSexNm())
                .dogImg(pet.getDogImg())
                .nosePrintImg(nosePrintImg)
                .build();
    }
}
