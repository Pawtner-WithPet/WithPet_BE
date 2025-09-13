package com.pawtner.WithPet.domain.search.realtime.dto;

import com.pawtner.WithPet.domain.search.realtime.entity.RealTimeSearch;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Builder
public class RealTimeSearchStatusResponse {
    private Long jobId;
    private Long userId;
    private Long petId;
    private String status;
    private boolean active;
    private LocalDateTime lastRunAt;
    private String keywords;

    public static RealTimeSearchStatusResponse from(RealTimeSearch e){
        return RealTimeSearchStatusResponse.builder()
                .jobId(e.getId())
                .userId(e.getUserId())
                .petId(e.getPetId())
                .status(e.getStatus())
                .active(e.isActive())
                .lastRunAt(e.getLastRunAt())
                .keywords(e.getKeywords())
                .build();
    }
}