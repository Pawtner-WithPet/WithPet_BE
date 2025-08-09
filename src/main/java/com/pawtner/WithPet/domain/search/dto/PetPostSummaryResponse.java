package com.pawtner.WithPet.domain.search.dto;

import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetPostSummaryResponse {

    private Long postId;              // 상세 이동용 ID
    private String type;              // "실종" | "발견"
    private String imgUrl;            // 사진
    private java.time.LocalDateTime eventDate;   // 실종/발견 날짜(통합 필드)
    private String location;          // 실종/발견 장소(통합 필드)
    private String kindNm;            // 견종
    private String sex;               // 성별(문자열 통일)
    private java.time.LocalDateTime createdAt;   // 정렬 기준

    // Lost → Summary
    public static PetPostSummaryResponse fromLost(LostPetPost p) {
        return PetPostSummaryResponse.builder()
                .postId(p.getLostPostId())
                .type("실종")
                .imgUrl(p.getPet().getDogImg())
                .eventDate(p.getLostDate())
                .location(p.getLostLocation())
                .kindNm(p.getPet().getKindNm())
                .sex(nullSafe(p.getPet().getSexNm()))
                .createdAt(p.getCreatedAt())
                .build();
    }

    // Found → Summary
    public static PetPostSummaryResponse fromFound(FoundPetPost p) {
        return PetPostSummaryResponse.builder()
                .postId(p.getFoundPostId())
                .type("발견")
                .imgUrl(p.getFoundPetImg())
                .eventDate(p.getFoundDate())
                .location(p.getFoundLocation())
                .kindNm(p.getKindNm())
                .sex(enumToString(p.getSex()))
                .createdAt(p.getCreatedAt())
                .build();
    }

    private static String enumToString(Object sexEnum) {
        return sexEnum == null ? null : sexEnum.toString();
    }
    private static String nullSafe(String s) {
        return s == null ? null : s;
    }
}
