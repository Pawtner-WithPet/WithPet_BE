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
public class FoundPostRequest {

    private Long owner;
    private Sex sex;
    private String kindNm;
    private LocalDateTime foundDate;

    private String foundLocation_latitude;
    private String foundLocation_longitude;
    private String description;

}
