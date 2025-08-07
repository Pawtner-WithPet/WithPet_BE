package com.pawtner.WithPet.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShelterSearchResponse {
    private String petImg;
    private String foundLocation;
    private String feature;

    private String shelterName;
    private String shelterLocation;
    private String shelterTel;
}
