package com.pawtner.WithPet.domain.walk.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutePointDto {
    private Integer seq;
    private BigDecimal lat;
    private BigDecimal lng;
}
