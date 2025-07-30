package com.pawtner.WithPet.domain.search.entity;

import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.pet.entity.Pet;
import com.pawtner.WithPet.global.enums.Sex;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "found_pet_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FoundPetPost {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long foundPostId;

    // Member: 발견글 작성자
    @ManyToOne(optional = false) //(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    // 발견견 이미지 URL
    @Column(name = "found_pet_img", length = 200, nullable = true)
    private String foundPetImg;

    // 성별
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sex sex;

    // 견종
    @Column(name = "kind_nm", length = 200, nullable = true)
    private String kindNm;

    // 비문 이미지 URL
    @Column(name = "nose_print_img", length = 200)
    private String nosePrintImg;

    // 발견 일시
    @Column(name = "found_date", nullable = false)
    private LocalDateTime foundDate;

    // 발견 장소
    @Column(name = "found_location", length = 200, nullable = false)
    private String foundLocation;


    // 추가 설명
    @Column(name = "description", length = 200, nullable = true)
    private String description;


    // 글 생성일(자동 세팅)
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Builder
    @Builder
    public FoundPetPost(Member owner,
                        String foundPetImg,
                        Sex sex,
                        String kindNm,
                        String nosePrintImg,
                        LocalDateTime foundDate,
                        String foundLocation,
                        String description) {

        this.owner          = owner;          // 작성자 (필수)
        this.foundPetImg    = foundPetImg;    // 발견견 이미지 URL(선택)
        this.sex            = sex;            // 성별 (필수)
        this.kindNm         = kindNm;         // 견종(선택)
        this.nosePrintImg   = nosePrintImg;   // 비문 이미지 URL(선택)
        this.foundDate      = foundDate;      // 발견 일시 (필수)
        this.foundLocation  = foundLocation;  // 발견 장소 (필수)
        this.description    = description;    // 추가 설명(선택)
    }
}
