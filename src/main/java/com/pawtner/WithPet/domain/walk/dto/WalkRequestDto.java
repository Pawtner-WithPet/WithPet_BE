package com.pawtner.WithPet.domain.walk.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalkRequestDto {

    private Long ownerId;
    private List<Long> petIds;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer durationSec;
    private Integer distanceM;
    private Double avgSpeed;
    private Boolean isSelected;

    private List<RoutePointDto> route;
    private List<PhotoDto> photos;

}
