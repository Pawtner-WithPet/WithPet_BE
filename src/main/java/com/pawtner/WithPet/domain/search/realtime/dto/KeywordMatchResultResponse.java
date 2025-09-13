package com.pawtner.WithPet.domain.search.realtime.dto;

import com.pawtner.WithPet.domain.search.realtime.entity.KeywordMatchResult;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class KeywordMatchResultResponse {
    private String sourceType;
    private String refKey;
    private String title;
    private String imageUrl;
    private String location;
    private String text;
    private LocalDateTime eventDate;
    private String matchedKeywords;
    private Integer matchedCount;
    private LocalDateTime createdAt;

    public static KeywordMatchResultResponse from(KeywordMatchResult e){
        return KeywordMatchResultResponse.builder()
                .sourceType(e.getSourceType())
                .refKey(e.getRefKey())
                .title(e.getTitle())
                .imageUrl(e.getImageUrl())
                .location(e.getLocation())
                .text(e.getText())
                .eventDate(e.getEventDate())
                .matchedKeywords(e.getMatchedKeywords())
                .matchedCount(e.getMatchedCount())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
