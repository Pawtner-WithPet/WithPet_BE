package com.pawtner.WithPet.domain.search.dto;

import com.pawtner.WithPet.domain.search.entity.FoundPetPost;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
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
public class LostPetPostResponse {

    private Long postId;
    private String type;
    private LocalDateTime lostDate;
    private String lostLocation;
    private String kindNm;
    private String sex;
    private String imgUrl;

    // Entity → DTO 변환 헬퍼
    public static LostPetPostResponse from(LostPetPost p) {
        return LostPetPostResponse.builder()
                .postId(p.getLostPostId())
                .type("실종")
                .lostDate(p.getLostDate())
                .lostLocation(p.getLostLocation())
                .kindNm(p.getPet().getKindNm())
                .sex(p.getPet().getSexNm())
                .imgUrl(p.getPet().getDogImg())
                .build();
    }
}