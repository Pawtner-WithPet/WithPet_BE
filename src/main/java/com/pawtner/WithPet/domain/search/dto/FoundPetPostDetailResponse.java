package com.pawtner.WithPet.domain.search.dto;

import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoundPetPostDetailResponse {
    private final String type;             // "발견" 고정
    private final Long postId;             // 글 ID
    private final Long ownerId;            // 작성자 ID

    private final String imgUrl;                         // 발견견 이미지 (foundPetImg)
    private final String kindNm;                         // 견종
    private final String sex;                            // 성별 (ENUM → 문자열)
    private final java.time.LocalDateTime foundDate;     // 발견 일시
    private final String foundLocation;                  // 발견 장소
    private final String description;                    // 추가 설명
    private final java.time.LocalDateTime createdAt;     // 작성일

    public static FoundPetPostDetailResponse from(FoundPetPost f) {
        return FoundPetPostDetailResponse.builder()
                .type("발견")
                .postId(f.getFoundPostId())
                .ownerId(f.getOwner().getId())
                .imgUrl(f.getFoundPetImg())
                .kindNm(f.getKindNm())
                .sex(enumToString(f.getSex()))
                .foundDate(f.getFoundDate())
                .foundLocation(f.getFoundLocation())
                .description(f.getDescription())
                .createdAt(f.getCreatedAt())
                .build();
    }
    private static String enumToString(Object e) { return e == null ? null : e.toString(); }
}
