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
public class LostPostRequest {

    private Long owner;
    private Long pet;

    private Integer height;
    private Integer weight;

    private LocalDateTime lostDate;
    private String lostLocation;
    private String favoritePlace;
    private String description;

}
