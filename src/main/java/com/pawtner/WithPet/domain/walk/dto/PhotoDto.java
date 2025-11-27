package com.pawtner.WithPet.domain.walk.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoDto {
    private Double lat;
    private Double lng;
    private LocalDateTime takenAt;
    private String url;
}
