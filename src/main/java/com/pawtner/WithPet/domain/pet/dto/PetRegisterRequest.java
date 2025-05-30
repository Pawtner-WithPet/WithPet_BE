package com.pawtner.WithPet.domain.pet.dto;

import lombok.*;

@Getter
@Setter
public class PetRegisterRequest {
    private String dogRegNo;
    private String rfidCd;
    private String ownerNm;
    private String ownerBirth;
    private Long userId;
}