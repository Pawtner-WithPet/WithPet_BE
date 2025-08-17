package com.pawtner.WithPet.domain.search.dto;

import com.pawtner.WithPet.domain.pet.entity.Pet;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LostStatusPetResponse {
    private final Long petId;              // 반려견 ID
    private final String dogNm;            // 이름
    private final String kindNm;           // 견종
    private final String sex;              // 성별
    private final Boolean status;          // 실종 여부 (실종 = true)

    public static LostStatusPetResponse from(Pet p) {
        return LostStatusPetResponse.builder()
                .petId(p.getId())
                .dogNm(p.getDogNm())
                .kindNm(p.getKindNm())
                .sex(p.getSexNm())
                .status(p.getStatus())
                .build();
    }
}
