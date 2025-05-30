package com.pawtner.WithPet.domain.pet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dogRegNo;
    private String rfidCd;
    private String dogNm;
    private String birthDt;
    private String sexNm;
    private String kindNm;
    private String neuterYn;
    private String orgNm;
    private String aprGbNm;
    private String officeTel;
    private String rfidGubun;

    private Long userId; // JWT 개발 전까지만 사용할 임시 userId 정보

    private String features;
    private String nosePrint;
    private String dogImg;

    @Builder
    public Pet(String dogRegNo, String rfidCd, String dogNm, String birthDt, String sexNm,
               String kindNm, String neuterYn, String orgNm, String aprGbNm, String officeTel,
               String rfidGubun, Long userId, String features, String nosePrint, String dogImg) {
        this.dogRegNo = dogRegNo;
        this.rfidCd = rfidCd;
        this.dogNm = dogNm;
        this.birthDt = birthDt;
        this.sexNm = sexNm;
        this.kindNm = kindNm;
        this.neuterYn = neuterYn;
        this.orgNm = orgNm;
        this.aprGbNm = aprGbNm;
        this.officeTel = officeTel;
        this.rfidGubun = rfidGubun;
        this.userId = userId;
        this.features = features;
        this.nosePrint = nosePrint;
        this.dogImg = dogImg;
    }
}