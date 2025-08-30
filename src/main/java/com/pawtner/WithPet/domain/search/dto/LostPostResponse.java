package com.pawtner.WithPet.domain.search.dto;

import com.pawtner.WithPet.global.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LostPostResponse {

    private Long postId;
    private String dogNm;
    private Sex sex;
    private String kindNm;
    private Integer height;
    private Integer weight;
    private String nosePrintImg;
    private String features;


    private LocalDateTime lostDate;
    private String lostLocation;
    private String favoritePlace;
    private String description;

}
