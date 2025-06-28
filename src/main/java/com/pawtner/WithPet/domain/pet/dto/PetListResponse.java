package com.pawtner.WithPet.domain.pet.dto;

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
}
