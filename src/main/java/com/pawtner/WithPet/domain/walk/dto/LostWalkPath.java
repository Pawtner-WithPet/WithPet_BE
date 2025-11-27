package com.pawtner.WithPet.domain.walk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LostWalkPath {
    private Long walkId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private List<RoutePointDto> route;
}
