package com.pawtner.WithPet.domain.search.dto;

import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.domain.search.entity.LostPetPost;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LostPetPostDetailResponse {
    private final String type;
    private final Long postId;
    private final Long ownerId; // 작성자 ID (채팅 등 후속 기능용)

    // Pet 기반 정보
    private final String imgUrl;           // 반려견 이미지
    private final String dogNm;            // 이름
    private final String kindNm;           // 견종
    private final String sex;              // 성별
    private final Integer age;             // 나이(년) — pet.birthDt 기반 계산
    private final String features;         // 특징

    // Post 기반 정보
    private final java.time.LocalDateTime lostDate;   // 실종 일시
    private final String lostLocation;                // 실종 장소
    private final String description;                 // 추가 설명
    private final Integer height;                     // 신장
    private final Integer weight;                     // 체중
    private final String favoritePlace;               // 익숙한 장소
    private final java.time.LocalDateTime createdAt;  // 작성일

    public static LostPetPostDetailResponse from(LostPetPost l) {
        Pet p = l.getPet();
        String petFavorite = null; // Pet.favoritePlace 필드가 실제로 존재하면 p.getFavoritePlace() 사용
        return LostPetPostDetailResponse.builder()
                .type("실종")
                .postId(l.getLostPostId())
                .ownerId(l.getOwner().getId())
                .imgUrl(p.getDogImg())
                .dogNm(p.getDogNm())
                .kindNm(p.getKindNm())
                .sex(safe(p.getSexNm()))
                .age(calcAgeFromBirthDt(p.getBirthDt()))
                .features(p.getFeatures())
                .lostDate(l.getLostDate())
                .lostLocation(l.getLostLocation())
                .description(l.getDescription())
                .height(l.getHeight())
                .weight(l.getWeight())
                .favoritePlace(firstNonNull(petFavorite, l.getFavoritePlace()))
                .createdAt(l.getCreatedAt())
                .build();
    }

    private static String safe(String s) { return s == null ? null : s; }

    // birthDt 포맷이 "yyyyMMdd"(예: 20200101) 라고 가정하고 만 나이(연) 계산
    private static Integer calcAgeFromBirthDt(String birthDt) {
        if (birthDt == null || birthDt.length() < 4) return null;
        try {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate birth = parseBirth(birthDt);
            if (birth == null) return null;
            return java.time.Period.between(birth, today).getYears();
        } catch (Exception e) { return null; }
    }
    private static java.time.LocalDate parseBirth(String birthDt) {
        try {
            String s = birthDt.trim();
            if (s.length() == 8) {
                return java.time.LocalDate.of(
                        Integer.parseInt(s.substring(0,4)),
                        Integer.parseInt(s.substring(4,6)),
                        Integer.parseInt(s.substring(6,8))
                );
            } else if (s.length() == 6) { // yyyymm 형태 대비
                return java.time.LocalDate.of(
                        Integer.parseInt(s.substring(0,4)),
                        Integer.parseInt(s.substring(4,6)),
                        1
                );
            } else if (s.length() == 4) {
                return java.time.LocalDate.of(Integer.parseInt(s), 1, 1);
            }
            return null;
        } catch (Exception e) { return null; }
    }

    private static String firstNonNull(String a, String b) { return a != null ? a : b; }
}
