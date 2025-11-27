package com.pawtner.WithPet.domain.search.entity;

import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "lost_pet_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LostPetPost {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long lostPostId;

    // Member: 실종글 작성자
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    // Pet: 실종 반려견
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    // 실종견 이미지 URL
    @Column(name = "lost_pet_img", length = 200, nullable = true)
    private String lostPetImg;

    // 실종 일시
    @Column(name = "lost_date", nullable = false)
    private LocalDateTime lostDate;

    // 실종 장소
    @Column(name = "lost_location", length = 200, nullable = false)
    private String lostLocation;

    // 실종 장소 - 위도
    @Column(name = "lost_location_latitude", nullable = false)
    private String lostLocation_latitude;

    // 실종 장소 - 경도
    @Column(name = "lost_location_longitude", nullable = false)
    private String lostLocation_longitude;

    // 익숙한 장소
    @Column(name = "favorite_place", length = 200, nullable = true)
    private String favoritePlace;

    // 추가 설명
    @Column(name = "description", length = 200, nullable = true)
    private String description;

    // 신장
    @Column(name = "height", nullable = true)
    private Integer height;

    // 체중
    @Column(name = "weight", nullable = true)
    private Integer weight;

    // 글 생성일(자동 세팅)
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Builder
    @Builder
    public LostPetPost(Member owner,
                       Pet pet,
                       String lostPetImg,
                       LocalDateTime lostDate,
                       String lostLocation,
                       String lostLocation_latitude,
                       String lostLocation_longitude,
                       String favoritePlace,
                       String description,
                       Integer height,
                       Integer weight) {

        this.owner         = owner;
        this.pet           = pet;
        this.lostPetImg    = lostPetImg;
        this.lostDate      = lostDate;
        this.lostLocation  = lostLocation;
        this.lostLocation_latitude  = lostLocation_latitude;
        this.lostLocation_longitude  = lostLocation_longitude;
        this.favoritePlace = favoritePlace;
        this.description   = description;
        this.height        = height;
        this.weight        = weight;
    }
}