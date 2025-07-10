package com.pawtner.WithPet.domain.noseprint.entity;

import com.pawtner.WithPet.domain.member.entity.Member;
import com.pawtner.WithPet.domain.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "nose_print")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NosePrint {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nose_print_id")
    private Long nose_print_id;

    // 이미지 url
    @Column(name = "nose_print_img", nullable = false, length = 200)
    private String nosePrintImg;

    // 업로드 시각(자동 세팅)
    @CreationTimestamp
    @Column(name = "register_datetime", nullable = false, updatable = false)
    private LocalDateTime registerDatetime;

    // Pet
    @OneToOne //(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", unique = true, nullable = true) //unique -> 개체 1마리 당 비문 1개만
    private Pet pet;

    // Member
    @ManyToOne //(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = true)
    private Member owner;

    // Builder
    @Builder
    public NosePrint(String nosePrintImg, Pet pet, Member owner) {
        this.nosePrintImg = nosePrintImg;
        this.pet = pet;
        this.owner = owner;
    }

    // 비문 이미지 수정 메서드
    public void updateImage(String newUrl) {
        this.nosePrintImg = newUrl;
    }
}
