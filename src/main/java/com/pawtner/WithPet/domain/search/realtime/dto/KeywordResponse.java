package com.pawtner.WithPet.domain.search.realtime.dto;

import com.pawtner.WithPet.domain.search.realtime.entity.RealTimeSearch;
import com.pawtner.WithPet.domain.search.realtime.support.KeywordUtils;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class KeywordResponse {
    private Long jobId;
    private Long petId;
    private String status;
    private boolean active;
    private List<String> keywords;
    private LocalDateTime createdAt;
    private LocalDateTime lastRunAt;

    public static KeywordResponse from(RealTimeSearch e) {
        return KeywordResponse.builder()
                .jobId(e.getId())
                .petId(e.getPetId())
                .status(e.getStatus())
                .active(e.isActive())
                .keywords(KeywordUtils.parse(e.getKeywords()))
                .createdAt(e.getCreatedAt())
                .lastRunAt(e.getLastRunAt())
                .build();
    }
}
