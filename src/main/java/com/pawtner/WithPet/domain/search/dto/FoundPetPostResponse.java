package com.pawtner.WithPet.domain.search.dto;

import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import com.pawtner.WithPet.global.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoundPetPostResponse {

    private Long postId;
    private String type;
    private LocalDateTime foundDate;
    private String foundLocation;
    private String kindNm;
    private Sex sex;
    private String imgUrl;

    // Entity → DTO 변환 헬퍼
    public static FoundPetPostResponse from(FoundPetPost p) {
        return FoundPetPostResponse.builder()
                .postId(p.getFoundPostId())
                .type("발견")
                .foundDate(p.getFoundDate())
                .foundLocation(p.getFoundLocation())
                .kindNm(p.getKindNm())
                .sex(p.getSex())
                .imgUrl(p.getFoundPetImg())
                .build();
    }
}
