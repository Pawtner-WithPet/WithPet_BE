package com.pawtner.WithPet.domain.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDetailResponse {
    private String dogRegNo;
    private String rfidCd;
    private String dogNm;
    private int dogAge;
    private String sexNm;
    private String kindNm;
    private String neuterYn;
    private String orgNm;
    private String aprGbNm;
    private String officeTel;
    private String rfidGubun;
    private Long userId;
    private String features;
    private String nosePrint;
    private String dogImg;
}
