package com.pawtner.WithPet.domain.walk.dto;

import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import lombok.*;

import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LostLocationResponse {
    private String latitude;
    private String longitude;
    private Long postId;

    public static LostLocationResponse fromEntity(LostPetPost lostPetPost) {
        return LostLocationResponse.builder()
                .latitude(formatLatLng(Double.valueOf(lostPetPost.getLostLocation_latitude())))
                .longitude(formatLatLng(Double.valueOf(lostPetPost.getLostLocation_longitude())))
                .postId(lostPetPost.getLostPostId())
                .build();
    }
    private static String formatLatLng(Double v) {
        if (v == null) return null;
        return String.format(Locale.US, "%.6f", v);
    }
}
