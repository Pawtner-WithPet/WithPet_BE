package com.pawtner.WithPet.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShelterApiResponse {
    private String desertionNo;      // 유기번호
    private String happenDt;         // 접수일자
    private String happenPlace;      // 발견장소

//    private String kindFullNm;       // 품종 전체명 (예: [개] 믹스견)
//    private String upKindCd;         // 축종코드 (예: 417000)
//    private String upKindNm;         // 축종명 (예: 개)
//    private String kindCd;           // 품종코드 (예: 000114)
    private String kindNm;           // 품종명 (예: 믹스견)

    private String colorCd;          // 색상
    private String age;              // 나이
    private String weight;           // 체중

    private String noticeNo;         // 공고번호
    private String noticeSdt;        // 공고시작일
    private String noticeEdt;        // 공고종료일

    private String popfile1;         // 사진1 URL
    private String popfile2;         // 사진2 URL

    private String processState;     // 상태 (보호중 등)
    private String sexCd;            // 성별 (M/F)
    private String neuterYn;         // 중성화 여부 (Y/N)
    private String specialMark;      // 특징

    private String careRegNo;        // 보호소 등록번호
    private String careNm;           // 보호소 이름
    private String careTel;          // 보호소 전화번호
    private String careAddr;         // 보호소 주소
    private String careOwnerNm;      // 보호소 소유자

    private String orgNm;            // 관할기관
//    private String updTm;            // 수정일시

    // 파생값: 대표 사진 (popfile1을 대표로 사용)
    public String getPopfile() {
        return popfile1;
    }
}